package kr.co.call.data.repositoryImpl

import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kr.co.call.domain.model.call.CallEndReason
import kr.co.call.domain.model.call.CallStreamingEvent
import kr.co.call.domain.model.call.CallStreamingFailureReason
import kr.co.call.domain.repository.CallStreamingRepository
import kr.co.call.network.dto.call.CallWebSocketMessageDto
import kr.co.call.network.websocket.CallWebSocketClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

private const val TAG = "CallSocket"

private const val CALL_READY_TIMEOUT_MILLIS = 5_000L
private const val NORMAL_CLOSE_CODE = 1_000

/**
 * 하나의 Coroutine에서 관리할 WebSocket 연결 상태입니다.
 */
private enum class ConnectionState {
    CONNECTING, // 핸드셰이크 미완료 상태
    WAITING_READY, // 서버 CALL_READY 기다리는 상태
    READY, // CALL_READY 완료
    TERMINATED, // 종료 또는 실패 이벤트 처리 상태
}

/**
 * 여러 스레드에서 발생한 WebSocket 콜백을 직렬화합니다.
 */
private sealed interface SocketSignal {

    data object Opened : SocketSignal

    data class TextReceived(
        val text: String,
    ) : SocketSignal

    data class BinaryReceived(
        val bytes: ByteString,
    ) : SocketSignal

    data class Closed(
        val code: Int,
        val reason: String,
    ) : SocketSignal

    data class Failed(
        val throwable: Throwable,
    ) : SocketSignal

    data object ReadyTimeout : SocketSignal
}


class CallStreamingRepositoryImpl @Inject constructor(
    private val callWebSocketClient: CallWebSocketClient,
    private val gson: Gson,
) : CallStreamingRepository {

    @Volatile
    private var isMicrophoneEnabled = true

    @Volatile
    private var activeWebSocket: WebSocket? = null

    override suspend fun setMicrophoneEnabled(enabled: Boolean) {
        isMicrophoneEnabled = enabled
    }

    override suspend fun sendAudio(pcmBytes: ByteArray) {
        val payload = if (isMicrophoneEnabled) pcmBytes else ByteArray(pcmBytes.size)
        activeWebSocket?.send(ByteString.of(*payload))
    }

    override fun connect(wsTicket: String): Flow<CallStreamingEvent> = callbackFlow {
        Timber.tag(TAG).d("connect() 호출: ticket 길이=%d", wsTicket.length)
        val output = this
        val signals = Channel<SocketSignal>(
            capacity = Channel.UNLIMITED, // 제어 이벤트는 빈도가 낮으므로 unlimited를 사용
        )
        var connectionState = ConnectionState.CONNECTING
        var activeCallId: Long? = null
        var readyTimeoutJob: Job? = null
        var currentWebSocket: WebSocket? = null

        // 종료 이벤트 전달
        fun emitTerminalEvent(event: CallStreamingEvent) {
            if (connectionState == ConnectionState.TERMINATED) return
            connectionState = ConnectionState.TERMINATED
            // 종료 시 타이머 종료
            readyTimeoutJob?.cancel()
            readyTimeoutJob = null
            output.trySend(event)
            output.close()
        }

        val signalProcessorJob = launch {
            for (signal in signals) {
                when (signal) {
                    SocketSignal.Opened -> {
                        Timber.tag(TAG).d("핸드셰이크 성공 (onOpen), CALL_READY 대기 시작")
                        // 연결 실패할 경우
                        if (connectionState != ConnectionState.CONNECTING) {
                            continue
                        }
                        connectionState = ConnectionState.WAITING_READY

                        // 핸드셰이크 성공 시점부터 CALL_READY 5초 측정
                        readyTimeoutJob?.cancel() // 기존 타이머가 있다면 초기화
                        readyTimeoutJob = launch {
                            delay(CALL_READY_TIMEOUT_MILLIS)
                            // 타이머도 직접 변경하지 않고 동일한 채널에 타임아웃 이벤트만 전달
                            signals.trySend(SocketSignal.ReadyTimeout)
                        }
                    }
                    is SocketSignal.TextReceived -> {
                        // 서버 json을 도메인 이벤트로 변환
                        val message = runCatching {
                            gson.fromJson(
                                signal.text,
                                CallWebSocketMessageDto::class.java,
                            )
                        }.getOrNull()

                        if (message == null) {
                            currentWebSocket?.cancel()
                            emitTerminalEvent(
                                CallStreamingEvent.Failed(
                                    reason = CallStreamingFailureReason.UNKNOWN,
                                ),
                            )
                            continue
                        }

                        when (message.type) {
                            "CALL_READY" -> {
                                val callId = message.data?.callId
                                if (
                                    connectionState != ConnectionState.WAITING_READY ||
                                    callId == null
                                ) {
                                    continue
                                }

                                activeCallId = callId
                                connectionState = ConnectionState.READY
                                readyTimeoutJob?.cancel()
                                readyTimeoutJob = null
                                Timber.tag(TAG).d("CALL_READY 수신: callId=%d", callId)
                                output.trySend(
                                    CallStreamingEvent.Ready(callId = callId),
                                )
                            }

                            "CALL_ENDED" -> {
                                val callId = message.data?.callId ?: continue
                                if (activeCallId != null && activeCallId != callId) {
                                    continue
                                }

                                Timber.tag(TAG).d(
                                    "CALL_ENDED 수신: callId=%d, reason=%s",
                                    callId,
                                    message.data?.reason,
                                )
                                emitTerminalEvent(
                                    CallStreamingEvent.Ended(
                                        callId = callId,
                                        reason = message.data?.reason.toCallEndReason(),
                                        callTimeSeconds = message.data?.callTime,
                                    ),
                                )
                            }

                            "ERROR" -> {
                                Timber.tag(TAG).e(
                                    "서버 ERROR 수신: reason=%s",
                                    message.data?.reason,
                                )
                                emitTerminalEvent(
                                    CallStreamingEvent.Failed(
                                        reason = message.data?.reason
                                            .toCallStreamingFailureReason(),
                                    ),
                                )
                            }

                            "AI_SPEECH_CANCELED" -> { // 끼어들기 했을 경우
                                val callId = message.data?.callId ?: continue
                                if (
                                    connectionState != ConnectionState.READY ||
                                    (activeCallId != null && activeCallId != callId)
                                ) {
                                    continue
                                }

                                output.trySend(
                                    CallStreamingEvent.SpeechCanceled(callId = callId),
                                )
                            }
                        }
                    }

                    is SocketSignal.BinaryReceived -> {
                        // Audio byteArray로 받음
                        output.trySend(
                            CallStreamingEvent.AudioReceived(
                                wav = signal.bytes.toByteArray(),
                            ),
                        )

                    }

                    is SocketSignal.Closed -> {
                        Timber.tag(TAG).d(
                            "소켓 종료 (onClosed): code=%d, reason=%s, state=%s",
                            signal.code,
                            signal.reason,
                            connectionState,
                        )
                        when (connectionState) {
                            ConnectionState.CONNECTING -> {
                                emitTerminalEvent(
                                    CallStreamingEvent.Failed(
                                        reason = CallStreamingFailureReason.HANDSHAKE_FAILED,
                                    ),
                                )
                            }

                            ConnectionState.WAITING_READY -> {
                                emitTerminalEvent(
                                    CallStreamingEvent.Failed(
                                        reason = CallStreamingFailureReason.CLOSED_BEFORE_READY,
                                    ),
                                )
                            }

                            ConnectionState.READY -> {
                                val callId = activeCallId
                                if (callId == null) {
                                    emitTerminalEvent(
                                        CallStreamingEvent.Failed(
                                            reason = CallStreamingFailureReason.UNKNOWN,
                                        ),
                                    )
                                    continue
                                }

                                emitTerminalEvent(
                                    CallStreamingEvent.Ended(
                                        callId = callId,
                                        reason = CallEndReason.UNKNOWN,
                                        callTimeSeconds = null,
                                    ),
                                )
                            }

                            ConnectionState.TERMINATED -> Unit
                        }
                    }

                    is SocketSignal.Failed -> {
                        Timber.tag(TAG).e(
                            signal.throwable,
                            "소켓 실패 (onFailure): state=%s",
                            connectionState,
                        )
                        val failureReason = when (connectionState) {
                            ConnectionState.CONNECTING ->
                                CallStreamingFailureReason.HANDSHAKE_FAILED

                            ConnectionState.WAITING_READY,
                            ConnectionState.READY,
                            -> CallStreamingFailureReason.NETWORK_ERROR

                            ConnectionState.TERMINATED -> continue
                        }

                        emitTerminalEvent(
                            CallStreamingEvent.Failed(reason = failureReason),
                        )
                    }

                    SocketSignal.ReadyTimeout -> {
                        if (connectionState != ConnectionState.WAITING_READY) {
                            continue
                        }

                        Timber.tag(TAG).w("CALL_READY 타임아웃 (5초 경과), 연결 종료")
                        currentWebSocket?.close(
                            NORMAL_CLOSE_CODE,
                            null,
                        )
                        emitTerminalEvent(
                            CallStreamingEvent.Failed(
                                reason = CallStreamingFailureReason.READY_TIMEOUT,
                            ),
                        )
                    }
                }
            }
        }

        val listener = object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket,
                response: Response,
            ) {
                currentWebSocket = webSocket
                activeWebSocket = webSocket
                signals.trySend(SocketSignal.Opened)
            }

            override fun onMessage(
                webSocket: WebSocket,
                text: String,
            ) {
                signals.trySend(SocketSignal.TextReceived(text))
            }

            override fun onMessage(
                webSocket: WebSocket,
                bytes: ByteString,
            ) {
                signals.trySend(SocketSignal.BinaryReceived(bytes))
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String,
            ) {
                signals.trySend(
                    SocketSignal.Closed(
                        code = code,
                        reason = reason,
                    ),
                )
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?,
            ) {
                if (response != null) {
                    Timber.tag(TAG).e(
                        "핸드셰이크 거부 응답: code=%d, headers=%s, body=%s",
                        response.code,
                        response.headers,
                        runCatching { response.body?.string() }.getOrNull(),
                    )
                }
                signals.trySend(SocketSignal.Failed(t))
            }
        }

        currentWebSocket = callWebSocketClient.open(
            wsTicket = wsTicket,
            listener = listener,
        )
        activeWebSocket = currentWebSocket

        awaitClose {
            readyTimeoutJob?.cancel()
            signalProcessorJob.cancel()
            signals.close()
            currentWebSocket?.cancel()
            if (activeWebSocket === currentWebSocket) {
                activeWebSocket = null
            }
        }
    }

    override suspend fun close() {
        activeWebSocket?.close(NORMAL_CLOSE_CODE, null)
        activeWebSocket = null
        isMicrophoneEnabled = true
    }
}

private fun String?.toCallEndReason(): CallEndReason =
    when (this) {
        "USER_ENDED" -> CallEndReason.USER_ENDED
        "TIMEOUT" -> CallEndReason.TIMEOUT
        else -> CallEndReason.UNKNOWN
    }

private fun String?.toCallStreamingFailureReason(): CallStreamingFailureReason =
    when (this) {
        "SERVER_ERROR" -> CallStreamingFailureReason.SERVER_ERROR
        else -> CallStreamingFailureReason.UNKNOWN
    }

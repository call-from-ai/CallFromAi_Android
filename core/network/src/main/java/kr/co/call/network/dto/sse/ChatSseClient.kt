package kr.co.call.network.dto.sse

import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kr.co.call.network.BuildConfig
import kr.co.call.network.dto.chatting.ChatSseMessageDto
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject

/**
 * SSE(Server-Sent Events)를 통해 채팅 메시지를 실시간으로 수신하는 클라이언트입니다.
 *
 * `GET /chat-rooms/subscribe` 엔드포인트에 연결하여 서버로부터 푸시되는
 * 채팅 메시지를 [Flow]로 변환해 제공합니다.
 *
 * Authorization 헤더는 [okHttpClient]에 등록된 [kr.co.call.network.interceptor.AuthInterceptor]가
 * 자동으로 추가합니다.
 */
class ChatSseClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    /**
     * SSE 서버에 연결하고 수신되는 채팅 메시지를 [Flow]로 반환합니다.
     *
     * - 수집(collect)을 시작하면 서버와 SSE 연결을 맺습니다.
     * - 수집이 취소되면 [EventSource.cancel]을 호출해 연결을 종료합니다.
     * - 서버가 연결을 닫으면 Flow가 정상 종료됩니다.
     * - 연결 실패 또는 네트워크 오류 발생 시 Flow가 예외와 함께 종료됩니다.
     *
     * @return 서버로부터 수신된 [ChatSseMessageDto]를 순서대로 emit하는 [Flow]
     */
    fun connect(): Flow<ChatSseMessageDto> = callbackFlow {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}chat-rooms/subscribe")
            .header("Accept", "text/event-stream")
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                runCatching {
                    gson.fromJson(data, ChatSseMessageDto::class.java)
                }.onSuccess { dto ->
                    trySend(dto)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                channel.close()
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                close(t ?: Exception("SSE 연결 실패 (code=${response?.code})"))
            }
        }

        val eventSource = EventSources.createFactory(okHttpClient)
            .newEventSource(request, listener)

        awaitClose { eventSource.cancel() }
    }
}

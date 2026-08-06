package kr.co.call.network.dto.sse

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kr.co.call.network.BuildConfig
import kr.co.call.network.dto.chatting.ChatSseNetworkEvent
import kr.co.call.network.dto.chatting.SseEventDto
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * SSE(Server-Sent Events)를 통해 채팅 메시지를 실시간으로 수신하는 클라이언트입니다.
 *
 * SSE 연결과 수신된 raw 이벤트의 JSON 파싱까지 담당하며,
 * 파싱된 [ChatSseNetworkEvent]를 Flow로 반환합니다.
 */
class ChatSseClient @Inject constructor(
    @Named("sseOkHttpClient") private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) {

    private var eventSource: EventSource? = null

    /**
     * SSE 서버에 연결하고 파싱된 이벤트를 Flow로 반환합니다.
     *
     * - collect를 시작하면 SSE 연결을 생성합니다.
     * - heartbeat 이벤트는 무시합니다.
     * - 파싱에 실패한 이벤트는 무시합니다.
     * - collect가 취소되거나 disconnect()가 호출되면 연결을 종료합니다.
     */
    fun connect(): Flow<ChatSseNetworkEvent> = callbackFlow {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}chat-rooms/subscribe")
            .header("Accept", "text/event-stream")
            .build()

        val listener = object : EventSourceListener() {

            override fun onOpen(eventSource: EventSource, response: Response) {
                Timber.d("SSE HTTP 연결 성공 (code=${response.code})")
            }

            /**
             * 서버로부터 SSE 이벤트를 수신했을 때 호출됩니다.
             *
             * "heartbeat" 타입의 이벤트는 무시하며, 그 외의 이벤트는 [SseEventDto]로 래핑하여
             * [ChatSseNetworkEvent]로 파싱한 후 Flow 스트림으로 전달합니다.
             *
             * @param eventSource 이벤트를 수신한 [EventSource] 객체
             * @param id 이벤트의 ID (선택 사항)
             * @param type 이벤트의 타입 (예: "connect", "message", "heartbeat" 등)
             * @param data 수신된 실제 데이터 문자열 (JSON 형태)
             */
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                if (type == "heartbeat") {
                    Timber.v("SSE heartbeat 수신")
                    return
                }
                Timber.d("SSE 이벤트 수신 (type=$type, data=$data)")
                SseEventDto(event = type, data = data)
                    .parse()
                    ?.let { trySend(it) }
            }

            override fun onClosed(eventSource: EventSource) {
                Timber.d("SSE 연결 종료")
                this@ChatSseClient.eventSource = null
                channel.close()
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                this@ChatSseClient.eventSource = null
                Timber.w(t, "SSE 연결 실패 (code=${response?.code})")
                channel.close()
            }
        }

        Timber.d("SSE EventSource 생성 중 (url=${request.url})")
        eventSource = EventSources.createFactory(okHttpClient)
            .newEventSource(request, listener)
        Timber.d("SSE EventSource 생성 완료, 서버 응답 대기 중")

        awaitClose {
            Timber.d("SSE awaitClose 호출됨 (flow 종료 또는 코루틴 취소)")
            disconnect()
        }
    }

    /**
     * 현재 SSE 연결을 종료합니다.
     */
    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
    }

    /**
     * [SseEventDto]에서 전달된 raw 데이터를 [ChatSseNetworkEvent]로 변환합니다.
     *
     * 처리되는 이벤트 타입:
     * - "connect": 연결 성공 이벤트
     * - "loading": 채팅방 로딩 중 이벤트 (chatRoomId 포함)
     * - "message": 새로운 채팅 메시지 수신 이벤트 (상세 메시지 정보 포함)
     * - "chat-error": 채팅 관련 에러 발생 이벤트 (chatRoomId 포함)
     *
     * @return 파싱에 성공하면 [ChatSseNetworkEvent] 객체를, 정의되지 않은 이벤트이거나
     * JSON 파싱 중 오류가 발생하면 null을 반환합니다.
     */
    private fun SseEventDto.parse(): ChatSseNetworkEvent? = runCatching {
        when (event) {
            "connect" -> ChatSseNetworkEvent.Connected
            "loading" -> {
                val chatRoomId = gson.fromJson(data, JsonObject::class.java)
                    .get("chatRoomId")?.asLong ?: return null
                ChatSseNetworkEvent.Loading(chatRoomId)
            }
            "message" -> {
                val json = gson.fromJson(data, JsonObject::class.java)
                ChatSseNetworkEvent.Message(
                    chatRoomId = json.get("chatRoomId").asLong,
                    chatMessageId = json.get("chatMessageId").asLong,
                    senderType = json.get("senderType").asString,
                    content = json.get("content").asString,
                    messageType = json.get("messageType").asString,
                    createdAt = json.get("createdAt").asString,
                )
            }
            "chat-error" -> {
                val chatRoomId = gson.fromJson(data, JsonObject::class.java)
                    .get("chatRoomId")?.asLong ?: return null
                ChatSseNetworkEvent.Failed(chatRoomId)
            }
            else -> null
        }
    }.getOrNull()
}

package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kr.co.call.common.di.ApplicationScope
import kr.co.call.data.mapper.ChatMapper.toDomain
import kr.co.call.domain.model.chatting.ChatSseEvent
import kr.co.call.domain.repository.ChatSseRepository
import kr.co.call.network.dto.sse.ChatSseClient
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSseRepositoryImpl @Inject constructor(
    private val chatSseClient: ChatSseClient,
    @ApplicationScope private val appScope: CoroutineScope,
) : ChatSseRepository {

    // SSE로 수신한 이벤트를 ChatListViewModel / ChatRoomViewModel에 브로드캐스트
    // extraBufferCapacity: 구독자가 느려도 최대 64개 이벤트까지 유실 없이 버퍼링
    private val _sseFlow = MutableSharedFlow<ChatSseEvent>(extraBufferCapacity = 64)
    override val sseFlow: SharedFlow<ChatSseEvent> = _sseFlow.asSharedFlow()

    // SSE 연결 및 재연결을 관리하는 Job.
    // 실제 SSE Transport 연결 여부는 ChatSseClient.isConnected에서 관리
    private var connectJob: Job? = null

    // 현재 SSE가 필요한 화면(ViewModel)의 수.
    // 0이 되면 SSE를 끊고, 0→1이 될 때 SSE를 연결한다.
    // 디버깅 용으로 추가.
    private val subscriberCount = AtomicInteger(0)

    // 현재 SSE Transport가 서버와 연결된 상태인지 반환한다.
    // connectJob의 활성 상태가 아닌 실제 EventSource 연결 상태를 기준으로 한다.
    override val isConnected: Boolean
        get() = chatSseClient.isConnected

    override fun connect() {
        val count = subscriberCount.incrementAndGet()
        Timber.d("SSE connect() 호출됨 (subscriberCount=$count)")
        // 재연결 루프가 이미 실행 중이면 중복 SSE 연결 생성을 방지한다.
        if (connectJob?.isActive == true) {
            Timber.d("SSE 이미 연결 중, 중복 connect() 무시")
            return
        }
        connectJob = appScope.launch {
            while (isActive) {
                // SSE 연결 종료 또는 실패 시 일정 시간 후 재연결을 시도한다.
                // 단, 실제 연결 상태(isConnected)는 ChatSseClient의 EventSource 상태를 따른다.
                runCatching {
                    chatSseClient.connect().collect { event ->
                        _sseFlow.emit(event.toDomain())
                    }
                }.onFailure { t ->
                    Timber.w(t, "SSE collect 중 예외 발생, 재연결 대기")
                }
                if (isActive) {
                    Timber.d("SSE 연결 종료, ${RECONNECT_DELAY_MILLIS}ms 후 재연결")
                    delay(RECONNECT_DELAY_MILLIS)
                }
            }
        }
    }

    override fun disconnect() {
        val count = subscriberCount.decrementAndGet()
        Timber.d("SSE disconnect() 호출됨 (subscriberCount=$count)")
        if (count > 0) {
            Timber.d("SSE 연결 유지 — 다른 구독자 존재")
            return
        }
        subscriberCount.set(0) // 음수 방지
        connectJob?.cancel()
        connectJob = null
        chatSseClient.disconnect()
    }

    private companion object {
        const val RECONNECT_DELAY_MILLIS = 3_000L
    }
}

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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSseRepositoryImpl @Inject constructor(
    private val chatSseClient: ChatSseClient,
    @ApplicationScope private val appScope: CoroutineScope,
) : ChatSseRepository {

    private val _sseFlow = MutableSharedFlow<ChatSseEvent>(extraBufferCapacity = 64)
    override val sseFlow: SharedFlow<ChatSseEvent> = _sseFlow.asSharedFlow()

    private var connectJob: Job? = null

    override fun connect() {
        if (connectJob?.isActive == true) {
            Timber.d("SSE 이미 연결 중, 중복 connect() 무시")
            return
        }
        Timber.d("SSE connect() 호출됨")
        connectJob = appScope.launch {
            while (isActive) {
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
        connectJob?.cancel()
        connectJob = null
        chatSseClient.disconnect()
    }

    private companion object {
        const val RECONNECT_DELAY_MILLIS = 3_000L
    }
}

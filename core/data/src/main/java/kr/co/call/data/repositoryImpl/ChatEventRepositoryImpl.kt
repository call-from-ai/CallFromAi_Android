package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.chatting.ChatEvent
import kr.co.call.domain.repository.ChatEventRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 채팅 관련 이벤트를 관리하고 브로드캐스트하는 [ChatEventRepository] 구현체.
 *
 * 이 저장소는 [SharedFlow]를 사용하여 구독자들에게 [ChatEvent] 스트림을 제공하며,
 * 채팅 관련 작업이 발생했을 때 실시간 UI 갱신이나 부가 동작(Side Effect)을 수행할 수 있도록 한다.
 *
 * 추후 SSE 연결이 추가되면 [ChatSseClient]를 주입받아 서버 푸시 이벤트(connect / message / heartbeat)를
 * 수신하고 동일한 [events] Flow를 통해 브로드캐스트할 예정이다.
 *
 * @property events 관찰자가 수집(collect)할 수 있는 [ChatEvent]의 Flow.
 */
class ChatEventRepositoryImpl @Inject constructor() : ChatEventRepository {

    private val _events = MutableSharedFlow<ChatEvent>(extraBufferCapacity = 16)
    override val events: SharedFlow<ChatEvent> = _events.asSharedFlow()

    override fun emitMessageSent(roomId: Long, content: String, createdTime: LocalDateTime) {
        _events.tryEmit(
            ChatEvent.MessageSent(
                roomId = roomId,
                content = content,
                whenSubmitted = TimeUtil.toTimeAgoText(createdTime),
            )
        )
    }
}

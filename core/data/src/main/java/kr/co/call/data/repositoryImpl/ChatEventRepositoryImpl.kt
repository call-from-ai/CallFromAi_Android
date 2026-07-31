package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.chatting.ChatEvent
import kr.co.call.domain.repository.ChatEventRepository
import java.time.LocalDateTime
import javax.inject.Inject

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

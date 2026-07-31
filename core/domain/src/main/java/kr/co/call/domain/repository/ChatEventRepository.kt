package kr.co.call.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kr.co.call.domain.model.chatting.ChatEvent
import java.time.LocalDateTime

interface ChatEventRepository {
    val events: SharedFlow<ChatEvent>
    fun emitMessageSent(roomId: Long, content: String, createdTime: LocalDateTime)
}

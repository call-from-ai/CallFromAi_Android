package kr.co.call.domain.model.chatting

import java.time.LocalDateTime

sealed interface ManagerChatItem {
    val id: String
    val createdAt: LocalDateTime
}

data class ManagerFirstMessage(
    override val id: String,
    val content: String,
    val type: ManagerFirstMessageType,
    override val createdAt: LocalDateTime,
): ManagerChatItem

enum class ManagerFirstMessageType {
    NORMAL,
    RELATIONSHIP // 대화를 많이 나눌수록, 더 자연스러운 관계가 생성돼요.
}
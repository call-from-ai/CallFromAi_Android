package kr.co.call.domain.model.chatting

import java.time.LocalDateTime

data class ManagerFirstMessage(
    val id: String,
    val content: String,
    val type: ManagerFirstMessageType,
    val createdAt: LocalDateTime,
)

enum class ManagerFirstMessageType {
    NORMAL,
    RELATIONSHIP // 대화를 많이 나눌수록, 더 자연스러운 관계가 생성돼요.
}
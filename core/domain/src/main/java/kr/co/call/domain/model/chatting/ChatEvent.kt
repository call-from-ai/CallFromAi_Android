package kr.co.call.domain.model.chatting

sealed interface ChatEvent {
    // 유저가 메시지 전송 성공 시 (낙관적 업데이트용)
    data class MessageSent(
        val roomId: Long,
        val content: String,
        val whenSubmitted: String,
    ) : ChatEvent
}

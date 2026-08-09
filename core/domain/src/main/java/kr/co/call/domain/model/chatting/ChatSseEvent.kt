package kr.co.call.domain.model.chatting

sealed interface ChatSseEvent {
    data object Connected : ChatSseEvent
    data class Loading(val chatRoomId: Long) : ChatSseEvent
    data class Message(val message: ChatSseMessage) : ChatSseEvent
    data class Failed(val chatRoomId: Long) : ChatSseEvent
}

data class ChatSseMessage(
    val chatRoomId: Long,
    val chatMessageId: Long,
    val senderType: SenderType,
    val content: String,
    val messageType: MessageType,
    val createdAt: String,
)
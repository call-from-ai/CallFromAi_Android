package kr.co.call.network.dto.chatting

data class SseEventDto(
    val event: String?,
    val data: String,
)

sealed interface ChatSseNetworkEvent {
    data object Connected : ChatSseNetworkEvent
    data class Loading(val chatRoomId: Long) : ChatSseNetworkEvent
    data class Message(
        val chatRoomId: Long,
        val chatMessageId: Long,
        val senderType: String,
        val content: String,
        val messageType: String,
        val createdAt: String,
    ) : ChatSseNetworkEvent
    data class Failed(val chatRoomId: Long) : ChatSseNetworkEvent
}

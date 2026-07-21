package kr.co.call.domain.model.chatting

data class ChatMessageList(
    val chatMessageList: List<ChatMessage>,
    val nextCursor: Long?,
    val hasNext: Boolean
)

data class ChatMessage(
    val chatMessageId: Long,
    val senderType: SenderType,
    val content: String = "",
    val messageType: MessageType,
    val photoUrl: String = "",
    val createdTime: String = "",
)

enum class SenderType {
    AI,
    USER,
    UNKNOWN;
    companion object {
        fun fromApiValue(apiValue: String): SenderType =
            entries.find { it.name == apiValue } ?: UNKNOWN
    }
}


enum class MessageType {
    TEXT,
    IMAGE,
    TEXT_IMAGE,
    UNKNOWN;
    companion object {
        fun fromApiValue(apiValue: String): MessageType =
            entries.find { it.name == apiValue } ?: UNKNOWN
    }
}
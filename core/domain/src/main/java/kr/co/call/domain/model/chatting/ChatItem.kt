package kr.co.call.domain.model.chatting

import java.time.LocalDate
import java.time.LocalDateTime

sealed interface ChatItem {

    data class Message(
        val chatMessageId: Long,
        val senderType: SenderType,
        val content: String = "",
        val messageType: MessageType,
        val photoUrl: String = "",
        val createdTime: LocalDateTime,
    ) : ChatItem

    data class DateSeparator(val date: LocalDate) : ChatItem
}

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

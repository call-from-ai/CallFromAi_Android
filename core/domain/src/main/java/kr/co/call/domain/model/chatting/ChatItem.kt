package kr.co.call.domain.model.chatting

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 채팅 대화 내에서 하나의 항목을 나타내는 Sealed Interface입니다.
 *
 * 채팅 목록에 표시될 수 있는 콘텐츠의 종류를 정의하며,
 * 실제 메시지 데이터와 날짜 구분선과 같은 UI 보조 요소를 구분합니다.
 *
 * @see Message 사용자가 전송하거나 AI가 생성한 실제 채팅 메시지를 나타냅니다.
 * @see DateSeparator UI에서 메시지를 날짜별로 그룹화하기 위한 구분 요소를 나타냅니다.
 */
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

package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class HomeNotification(
    val notificationId: Long,
    val type: NotificationType,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val characterName: String?,
    val profileImageUrl: String?,
)

enum class NotificationType(
    val title: String,
) {
    MISSED_CALL(title = "부재중 전화"),
    ANNIVERSARY(title = "기념일"),
    CALL_RESERVATION(title = "통화 약속"),
}

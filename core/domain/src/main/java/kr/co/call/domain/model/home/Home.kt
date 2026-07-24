package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class HomeSummary(
    val firstName: String,
    val relationshipDays: Int,
    val totalCallCount: Int,
    val callStreakDays: Int,
)

data class HomeCharacter(
    val id: Long,
    val name: String,
    val relationshipDays: Int,
    val imageUrl: String?,
    val isMain: Boolean,
)

data class CallReservation(
    val id: Long,
    val characterId: Long,
    val firstName: String,
    val imageUrl: String?,
    val scheduledAt: LocalDateTime,
)

data class CallReservations(
    val totalCount: Int,
    val items: List<CallReservation>,
)

data class CallHistory(
    val callId: Long,
    val characterName: String,
    val aiSummary: String,
    val startedAt: LocalDateTime,
    val isOutgoing: Boolean,
    val isMissed: Boolean,
)

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

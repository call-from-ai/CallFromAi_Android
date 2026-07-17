package kr.co.call.impl.viewmodel.state

import kr.co.call.impl.viewmodel.NotificationType
import java.time.LocalDateTime

/**
 * 알림
 */
data class HomeNotificationState(
    val notificationId: Long,
    val type: NotificationType,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val characterName: String?,
    val profileImageUrl: String?,
)

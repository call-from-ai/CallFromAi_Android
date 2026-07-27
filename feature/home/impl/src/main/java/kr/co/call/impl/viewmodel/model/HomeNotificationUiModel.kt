package kr.co.call.impl.viewmodel.model

import kr.co.call.domain.model.home.NotificationType

data class HomeNotificationUiModel(
    val notificationId: Long,
    val type: NotificationType,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAtText: String,
    val characterName: String?,
    val profileImageUrl: String?,
)

package kr.co.call.network.dto.home

data class HomeNotificationDto(
    val notificationId: Long,
    val type: String,
    val title: String,
    val content: String,
    val read: Boolean,
    val createdAt: String,
    val characterId: Long?,
    val characterName: String?,
    val characterImageUrl: String?,
)

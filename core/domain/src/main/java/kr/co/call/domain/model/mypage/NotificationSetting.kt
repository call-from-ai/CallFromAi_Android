package kr.co.call.domain.model.mypage

data class NotificationSetting(
    val allNotificationEnabled: Boolean,
    val nightCallAllowed: Boolean,
    val doNotDisturbStart: String? = null,
    val doNotDisturbEnd: String? = null,
)

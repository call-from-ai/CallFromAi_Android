package kr.co.call.network.dto.mypage

data class NotificationSettingDto(
    val allNotificationEnabled: Boolean = true,
    val nightCallAllowed: Boolean = true,
    val doNotDisturbStart: String? = null,
    val doNotDisturbEnd: String? = null,
)

data class NotificationSettingUpdateRequestDto(
    val allNotificationEnabled: Boolean? = null,
    val nightCallAllowed: Boolean? = null,
)

data class DoNotDisturbUpdateRequestDto(
    val startTime: String,
    val endTime: String,
)

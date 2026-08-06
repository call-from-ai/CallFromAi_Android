package kr.co.call.impl.viewmodel

sealed interface ProfileIntent {
    data object Refresh : ProfileIntent
    data object ClickEditProfile : ProfileIntent
    data object ClickSubscription : ProfileIntent
    data class ToggleAllNotification(val enabled: Boolean) : ProfileIntent
    data class ToggleLateNightCall(val enabled: Boolean) : ProfileIntent
    data object ClickDisturbTime : ProfileIntent
    data object ClickCallTimeManagement : ProfileIntent
}

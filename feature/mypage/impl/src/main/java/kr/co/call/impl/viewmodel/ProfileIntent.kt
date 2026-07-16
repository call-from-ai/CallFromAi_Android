package kr.co.call.impl.viewmodel

sealed interface ProfileIntent {
    data object ClickEditProfile : ProfileIntent
    data object ClickSubscription : ProfileIntent
    data class ToggleAllNotification(val enabled: Boolean) : ProfileIntent
    data class ToggleLateNightCall(val enabled: Boolean) : ProfileIntent
    data object ClickDoNotDisturbTime : ProfileIntent
    data object ClickCallTimeManagement : ProfileIntent
}
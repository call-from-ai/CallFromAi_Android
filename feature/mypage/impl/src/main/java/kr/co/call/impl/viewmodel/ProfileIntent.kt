package kr.co.call.impl.viewmodel

sealed interface ProfileIntent {
    data object Refresh : ProfileIntent

    /** 수정 화면에서 저장 직후 로컬 즉시 반영 */
    data class ApplyLocalUpdate(
        val nickname: String,
        val profileImageUrl: String,
    ) : ProfileIntent

    data object ClickEditProfile : ProfileIntent
    data object ClickSubscription : ProfileIntent
    data class ToggleAllNotification(val enabled: Boolean) : ProfileIntent
    data class ToggleLateNightCall(val enabled: Boolean) : ProfileIntent
    data object ClickDisturbTime : ProfileIntent
    data object ClickCallTimeManagement : ProfileIntent
}

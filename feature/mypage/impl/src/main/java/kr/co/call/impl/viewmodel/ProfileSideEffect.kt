package kr.co.call.impl.viewmodel

sealed interface ProfileSideEffect {
    data object NavigateToEditProfile : ProfileSideEffect
    data object NavigateToSubscription : ProfileSideEffect
    data object NavigateToDoNotDisturbTime : ProfileSideEffect
    data object NavigateToCallTimeManagement : ProfileSideEffect
}
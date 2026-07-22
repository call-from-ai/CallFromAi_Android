package kr.co.call.impl.viewmodel

sealed interface LandingSideEffect {
    data object NavigateToLogin: LandingSideEffect
    data object NavigateToHome: LandingSideEffect
}
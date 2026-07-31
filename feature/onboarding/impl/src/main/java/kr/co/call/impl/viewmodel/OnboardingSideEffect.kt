package kr.co.call.impl.viewmodel

sealed interface OnboardingSideEffect{
    data class OnboardingCompleted(
        val callNow:Boolean,
    ): OnboardingSideEffect

    data class ShowMessage(
        val message: String,
    ):OnboardingSideEffect
}
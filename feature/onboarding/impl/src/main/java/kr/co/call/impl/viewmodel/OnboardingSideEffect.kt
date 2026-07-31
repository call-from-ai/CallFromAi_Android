package kr.co.call.impl.viewmodel

sealed interface OnboardingSideEffect{
    data object OnboardingSubmitted : OnboardingSideEffect

    data class ShowMessage(
        val message: String,
    ):OnboardingSideEffect
}

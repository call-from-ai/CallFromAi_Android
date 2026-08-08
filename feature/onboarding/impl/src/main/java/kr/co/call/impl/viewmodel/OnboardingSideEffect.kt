package kr.co.call.impl.viewmodel

sealed interface OnboardingSideEffect {
    data object OnboardingSubmitted : OnboardingSideEffect

    /** 캐릭터 추가 제출 완료 -> 홈 (전화 화면 스킵) */
    data object AdditionalCharacterCreated : OnboardingSideEffect

    data class ShowMessage(
        val message: String,
    ) : OnboardingSideEffect
}

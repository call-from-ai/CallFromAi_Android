package kr.co.call.impl.viewmodel

/**
 * 로그인 과정에서 한 번만 실행되어야 하는 동작을 정의한다.
 * 로그인 성공 시 화면을 이동하고 실패 시 오류 메시지를 전달한다.
 */
sealed interface LoginSideEffect{
    data class NavigateToAgreement(
        val needsOnboarding: Boolean,
    ): LoginSideEffect
    data class NavigateToOnboarding(
        val needsOnboarding: Boolean,
    ): LoginSideEffect
    data class NavigateToHome(
        val needsOnboarding: Boolean,
    ): LoginSideEffect
    data class ShowError(
        val message: String,
    ): LoginSideEffect
}


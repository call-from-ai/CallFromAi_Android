package kr.co.call.impl.viewmodel

/**
 * 로그인 과정에서 한 번만 실행되어야 하는 동작을 정의한다.
 * 로그인 성공 시 화면을 이동하고 실패 시 오류 메시지를 전달한다.
 */
sealed interface LoginSideEffect{
    data object NavigateToAgreement: LoginSideEffect
    data object NavigateToOnboarding: LoginSideEffect
    data object NavigateToHome: LoginSideEffect
    data class ShowError(
        val message: String,
    ): LoginSideEffect
}


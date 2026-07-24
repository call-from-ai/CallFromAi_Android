package kr.co.call.impl.viewmodel

/**
 * 로그인 과정에서 한 번만 실행되어야 하는 동작을 정의한다.
 * 로그인 성공 시 화면을 이동하고 실패 시 오류 메시지를 전달한다.
 */
sealed interface LoginSideEffect {
    // 서버 로그인 성공 후 다음 화면인 약관 화면으로 이동
    data object NavigateToNext: LoginSideEffect

    // 로그인 실패 원인을 화면 또는 로그로 전달
    data class ShowError(
        val message: String,
    ): LoginSideEffect
}
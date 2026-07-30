package kr.co.call.impl.viewmodel

/**
 * 자동 로그인 확인 결과에 따른 화면 이동 동작
 * 토큰이 없으면 로그인 화면, 있으면 홈 화면으로 이동한다. (자동로그인)
 */
sealed interface LandingSideEffect {
    data object NavigateToLogin: LandingSideEffect
    data object NavigateToHome: LandingSideEffect
}
package kr.co.call.impl.viewmodel.state

/**
 * 랜딩 화면에서 자동 로그인 확인 여부를 관리하는 상태
 * 토큰 확인이 완료되면 false로 변경된다.
 */
data class LandingUiState(
    val isCheckingAutoLogin:Boolean=true,
)
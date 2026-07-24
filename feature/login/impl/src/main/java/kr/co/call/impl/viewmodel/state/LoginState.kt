package kr.co.call.impl.viewmodel.state

/**
 * 로그인 화면에서 관리하는 UI 상태
 * 로그인 요청 진행 여부, 성공 여부, 오류 메시지를 저장한다.
 */
data class LoginState (
    val isLoading: Boolean=false,
    val isLoginSuccess: Boolean=false,
    val errorMessage: String?=null,
)
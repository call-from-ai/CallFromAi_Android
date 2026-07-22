package kr.co.call.impl.viewmodel.state

data class LoginState (
    val isLoading: Boolean=false,
    val isLoginSuccess: Boolean=false,
    val errorMessage: String?=null,
)
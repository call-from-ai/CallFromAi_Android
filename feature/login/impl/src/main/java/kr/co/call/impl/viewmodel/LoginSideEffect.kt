package kr.co.call.impl.viewmodel

sealed interface LoginSideEffect {
    data object NavigateToNext: LoginSideEffect
    data class ShowError(
        val message: String,
    ): LoginSideEffect
}
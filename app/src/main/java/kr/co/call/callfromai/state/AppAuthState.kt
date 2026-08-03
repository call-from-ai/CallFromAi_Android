package kr.co.call.callfromai.state

sealed interface AppAuthState {
    data object Loading: AppAuthState
    data object Authenticated: AppAuthState
    data object Unauthenticated: AppAuthState
}
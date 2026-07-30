package kr.co.call.callfromai

sealed interface AppAuthState {
    data object Loading : AppAuthState
    data object Unauthenticated : AppAuthState
    data object Authenticated : AppAuthState
}

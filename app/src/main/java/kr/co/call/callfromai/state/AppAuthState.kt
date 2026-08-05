package kr.co.call.callfromai.state

sealed interface AppAuthState {
    data object Loading: AppAuthState
    data class Authenticated(
        val needsOnboarding: Boolean,
    ): AppAuthState
    data object Unauthenticated: AppAuthState
}
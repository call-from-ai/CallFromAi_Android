package kr.co.call.impl.viewmodel

sealed interface CallSideEffect {
    data object FinishCall : CallSideEffect

    data class ShowMessage(val message: String) : CallSideEffect
}

package kr.co.call.impl.viewmodel

sealed interface CallSideEffect {
    data object FinishCall : CallSideEffect
}

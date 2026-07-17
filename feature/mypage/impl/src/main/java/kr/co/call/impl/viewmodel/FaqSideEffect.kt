package kr.co.call.impl.viewmodel

sealed interface FaqSideEffect {
    data class ShowError(val message: String) : FaqSideEffect
}
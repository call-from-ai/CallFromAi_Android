package kr.co.call.impl.viewmodel

sealed interface TermSideEffect {
    data class ShowMessage(
        val message: String,
    ) : TermSideEffect
}

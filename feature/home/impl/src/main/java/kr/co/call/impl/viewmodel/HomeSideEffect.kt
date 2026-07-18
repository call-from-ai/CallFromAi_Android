package kr.co.call.impl.viewmodel

sealed interface HomeSideEffect {
    data class ShowMessage(
        val message: String,
        val actionLabel: String? = null,
        val onActionClick: (() -> Unit)? = null,
    ) : HomeSideEffect

}

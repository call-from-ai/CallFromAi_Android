package kr.co.call.impl.viewmodel

sealed interface CallIncomingSideEffect {
    data class RequestMicrophonePermission(
        val callId: String,
    ) : CallIncomingSideEffect

    data class NavigateToCall(
        val callId: String,
    ) : CallIncomingSideEffect

    data object Finish : CallIncomingSideEffect

    data class ShowMessage(
        val message: String,
    ) : CallIncomingSideEffect
}
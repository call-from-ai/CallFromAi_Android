package kr.co.call.impl.viewmodel

sealed interface CallIncomingSideEffect {
    data class RequestMicrophonePermission(
        val callId: Long,
    ) : CallIncomingSideEffect

    data class NavigateToCall(
        val callId: Long,
        val characterId: Long,
    ) : CallIncomingSideEffect

    data object Finish : CallIncomingSideEffect

    data class ShowMessage(
        val message: String,
    ) : CallIncomingSideEffect
}

package kr.co.call.impl.viewmodel

sealed interface CallIncomingIntent {
    data class Initialize(
        val callId: Long,
        val characterId: Long,
    ) : CallIncomingIntent

    data object AcceptCall : CallIncomingIntent

    data class MicrophonePermissionResult(
        val isGranted: Boolean,
    ) : CallIncomingIntent

    data object RejectCall : CallIncomingIntent
}

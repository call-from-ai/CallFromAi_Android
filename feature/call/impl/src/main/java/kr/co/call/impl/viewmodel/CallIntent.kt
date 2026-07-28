package kr.co.call.impl.viewmodel

import kr.co.call.impl.viewmodel.model.CallDirection

sealed interface CallIntent {
    data class Initialize(
        val callId: Long,
        val characterId: Long,
        val direction: CallDirection,
    ) : CallIntent

    data class MicrophonePermissionResult(
        val isGranted: Boolean,
    ) : CallIntent

    data object EndCall : CallIntent

    data object ToggleMicrophone : CallIntent

    data object ToggleSpeaker : CallIntent
}

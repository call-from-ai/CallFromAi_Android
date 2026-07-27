package kr.co.call.impl.viewmodel

sealed interface CallIntent {
    data class Initialize(
        val characterId: Long,
        val characterName: String,
    ) : CallIntent

    data class MicrophonePermissionResult(
        val isGranted: Boolean,
    ) : CallIntent

    data object EndCall : CallIntent

    data object ToggleMicrophone : CallIntent

    data object ToggleSpeaker : CallIntent
}

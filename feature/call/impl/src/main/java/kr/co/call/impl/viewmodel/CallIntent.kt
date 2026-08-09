package kr.co.call.impl.viewmodel

sealed interface CallIntent {
    data class Initialize(
        val callId: Long,
        val characterId: Long,
        val isIncoming: Boolean,
        val characterName: String = "",
        val characterImageUrl: String? = null,
    ) : CallIntent

    data class MicrophonePermissionResult(
        val isGranted: Boolean,
    ) : CallIntent

    /**
     * 서버/WebSocket에서 실제 통화 연결 완료를 확인했을 때 전달합니다.
     */
    data object CallConnected : CallIntent

    data object EndCall : CallIntent

    data object ToggleMicrophone : CallIntent

    data object ToggleSpeaker : CallIntent
}

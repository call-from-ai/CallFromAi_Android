package kr.co.call.domain.model.call

// 통화 세션 상태
data class CallSessionState(
    val isMicrophoneEnabled: Boolean = true,
    val isSpeakerEnabled: Boolean = false,
    val isBluetoothConnected: Boolean = false,
)

// 통화 오디오 상태
enum class CallAudioFocusState {
    IDLE,
    GAINED,
    LOST_TRANSIENT,
    LOST,
}

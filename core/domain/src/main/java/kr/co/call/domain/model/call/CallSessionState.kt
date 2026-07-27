package kr.co.call.domain.model.call

data class CallSessionState(
    val isMicrophoneEnabled: Boolean = true,
    val isSpeakerEnabled: Boolean = false,
)

// 통화 오디오 상태
enum class CallAudioFocusState {
    IDLE,
    GAINED,
    LOST_TRANSIENT,
    LOST,
}

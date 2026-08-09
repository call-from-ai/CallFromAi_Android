package kr.co.call.domain.model.call

/**
 * 통화 WebSocket에서 전달되는 상태 이벤트입니다.
 */
sealed interface CallStreamingEvent {

    /**
     * 서버가 통화 준비를 완료한 상태입니다.
     */
    data class Ready(
        val callId: Long,
    ) : CallStreamingEvent

    /**
     * 서버가 통화를 정상적으로 종료한 상태입니다.
     */
    data class Ended(
        val callId: Long,
        val reason: CallEndReason,
        val callTimeSeconds: Long?,
    ) : CallStreamingEvent

    /**
     * 연결 또는 통화 처리에 실패한 상태입니다.
     */
    data class Failed(
        val reason: CallStreamingFailureReason,
    ) : CallStreamingEvent

    /**
     * 사용자가 AI 발화에 끼어들어 서버가 재생 큐를 비우라고 알린 상태입니다.
     * 통화 종료가 아니며 연결은 계속 유지됩니다.
     */
    data class SpeechCanceled(
        val callId: Long,
    ) : CallStreamingEvent

    /**
     *
     */
    data class AudioReceived(
        val wav: ByteArray,
    ): CallStreamingEvent

}

/**
 * 서버가 전달하는 정상 통화 종료 사유입니다.
 */
enum class CallEndReason {
    USER_ENDED,
    TIMEOUT,
    UNKNOWN,
}

/**
 * WebSocket 연결 과정에서 발생할 수 있는 실패 사유입니다.
 */
enum class CallStreamingFailureReason {
    SERVER_ERROR,
    READY_TIMEOUT,
    HANDSHAKE_FAILED,
    CLOSED_BEFORE_READY,
    NETWORK_ERROR,
    UNKNOWN,
}

package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.call.CallSessionState
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.model.CallDirection

/**
 * 통화 중에 사용되는 상태
 */
data class CallState(
    val callId: Long = 0L,
    val characterId: Long = 0L,
    val character: CallCharacterUiModel = CallCharacterUiModel(),
    val direction: CallDirection = CallDirection.OUTGOING,
    val phase: CallPhase = CallPhase.CONNECTING,
    val durationSeconds: Int = 0,
    val endedDurationSeconds: Int? = null,
    val sessionState: CallSessionState = CallSessionState(),
)

// 통화 상태
enum class CallPhase {
    CONNECTING,
    READY,
    ENDING,
    ENDED,
    ERROR,
}

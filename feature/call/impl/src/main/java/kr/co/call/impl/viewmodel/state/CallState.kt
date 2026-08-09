package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.home.CallInfo

/**
 * 통화의 진행 자체를 나타내는 상태입니다.
 *
 * 캐릭터 표시 정보와 오디오 장치 상태는 서로 수명이 다르므로
 * 이 상태에 포함하지 않고 각각의 소유자에게서 별도로 관찰합니다.
 */
data class CallState(
    val callId: Long = 0L,
    val characterId: Long = 0L,
    val isIncoming: Boolean = false,
    val phase: CallPhase = CallPhase.CONNECTING,
    val startedAtMillis: Long? = null,
    val durationSeconds: Int = 0,
    val hasReceivedCallReady: Boolean = false, // CALL_READY를 한번이라도 받았는지 여부로, 종료 화면 표시 분기에 사용
    val callInfo: CallInfo? = null,
    // ENDING 중 통화 요약 조회 대기 구간인지. ENDING 문구 분기에 사용
    val isPreparingSummary: Boolean = false,
)

// 통화 상태
enum class CallPhase {
    CONNECTING,
    READY,
    ENDING,
    ENDED,
    ERROR,
}

package kr.co.call.impl.component.record

/**
 * 통화 녹음 플레이어에 표시되는 재생 상태
 *
 */
data class CallRecordPlayerState(
    val currentPositionMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val isPlaying: Boolean = false,
)

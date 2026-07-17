package kr.co.call.impl.viewmodel.state

import kr.co.call.impl.viewmodel.CallHistoryType

/**
 * 통화 기록 조회 개별 아이템
 */
data class HomeCallHistoryItemState(
    val callId: Long,
    val characterName: String,
    val aiSummary: String,
    val startedAtText: String,
    val type: CallHistoryType,
)

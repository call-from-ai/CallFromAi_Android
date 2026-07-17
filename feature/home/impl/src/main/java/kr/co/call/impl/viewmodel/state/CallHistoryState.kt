package kr.co.call.impl.viewmodel.state

/**
 * 통화 기록 List
 */
data class CallHistoryState(
    val histories: List<HomeCallHistoryItemState> = emptyList(),
)

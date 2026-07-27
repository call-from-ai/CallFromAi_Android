package kr.co.call.impl.viewmodel.model

data class CallHistoryUiModel(
    val callId: Long,
    val characterName: String = "",
    val aiSummary: String = "",
    val startedAtText: String = "",
    val iconType: CallHistoryIconType,
)

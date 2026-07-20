package kr.co.call.network.dto

data class CallHistoryListResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CallHistoryPageDto,
)

data class CallHistoryPageDto(
    val content: List<CallHistoryDto>,
    val hasNext: Boolean,
)

data class CallHistoryDto(
    val callId: Long,
    val characterName: String,
    val sender: String,
    val aiSummary: String,
    val startedAt: String,
    val callType: String,
    val status: String,
)

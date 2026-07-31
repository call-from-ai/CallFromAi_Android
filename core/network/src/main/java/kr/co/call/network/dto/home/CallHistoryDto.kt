package kr.co.call.network.dto.home

data class CallHistoryPageDto(
    val content: List<CallHistoryDto>,
)

data class CallHistoryDto(
    val callId: Long,
    val characterName: String,
    val sender: String,
    val aiSummary: String? = null,
    val createdAt: String,
    val status: String,
)

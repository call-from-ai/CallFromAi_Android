package kr.co.call.network.dto.home

data class CallTranscriptDto(
    val callId: Long,
    val lines: List<CallScriptMessage>,
)

data class CallScriptMessage(
    val speaker: String,
    val content: String,
    val createdAt: String,
)

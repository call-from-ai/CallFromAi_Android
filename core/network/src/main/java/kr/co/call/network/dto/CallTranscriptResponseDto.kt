package kr.co.call.network.dto

data class CallTranscriptResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CallTranscriptResultDto,
)

data class CallTranscriptResultDto(
    val content: List<CallTranscriptDto>,
)

data class CallTranscriptDto(
    val speaker: String,
    val content: String,
)

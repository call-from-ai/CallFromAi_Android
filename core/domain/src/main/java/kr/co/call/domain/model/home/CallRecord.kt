package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class CallInfo(
    val callId: Long,
    val title: String,
    val calledAt: LocalDateTime,
    val characterName: String,
    val recordingUrl: String?,
    val durationMillis: Long,
)

data class CallTranscript(
    val content: String,
    val speaker: Speaker,
) {
    enum class Speaker {
        USER,
        AI,
    }
}

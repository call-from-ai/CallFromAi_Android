package kr.co.call.domain.model.home

data class CallTranscript(
    val content: String,
    val speaker: Speaker,
) {
    enum class Speaker {
        USER,
        AI,
    }
}

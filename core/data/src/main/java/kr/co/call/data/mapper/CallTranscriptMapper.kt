package kr.co.call.data.mapper

import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.network.dto.CallTranscriptDto
import kr.co.call.network.dto.CallTranscriptResultDto

internal fun CallTranscriptResultDto.toDomain(): List<CallTranscript> =
    content.map { transcript -> transcript.toDomain() }

private fun CallTranscriptDto.toDomain(): CallTranscript =
    CallTranscript(
        content = content,
        speaker = speaker.toDomainSpeaker(),
    )

private fun String.toDomainSpeaker(): CallTranscript.Speaker =
    when (this) {
        "사용자", "USER" -> CallTranscript.Speaker.USER
        "AI" -> CallTranscript.Speaker.AI
        else -> error("지원하지 않는 화자입니다: $this")
    }

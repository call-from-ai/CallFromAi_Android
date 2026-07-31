package kr.co.call.data.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.network.dto.home.CallScriptMessage
import kr.co.call.network.dto.home.CallTranscriptDto

internal fun CallTranscriptDto.toDomain(): List<CallTranscript> =
    lines.map { line -> line.toDomain() }

private fun CallScriptMessage.toDomain(): CallTranscript =
    CallTranscript(
        content = content,
        speaker = speaker.toDomainSpeaker(),
        createdAt = TimeUtil.parseLocalDateTime(createdAt),
    )

private fun String.toDomainSpeaker(): CallTranscript.Speaker =
    when (this) {
        "사용자", "USER" -> CallTranscript.Speaker.USER
        "AI" -> CallTranscript.Speaker.AI
        else -> error("지원하지 않는 화자입니다: $this")
    }

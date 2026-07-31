package kr.co.call.data.mapper

import java.time.LocalDateTime
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.network.dto.home.CallDetailDto
import kr.co.call.network.dto.home.CallScriptMessage
import kr.co.call.network.dto.home.CallTranscriptDto

internal fun CallDetailDto.toDomain(callId: Long): CallInfo =
    CallInfo(
        callId = callId,
        title = aiSummary
            ?.takeIf { summary -> summary.isNotBlank() }
            ?: DEFAULT_CALL_TITLE,
        calledAt = createdAt.toLocalDateTimeOrNow(),
        characterName = characterName
            ?.takeIf { name -> name.isNotBlank() }
            ?: DEFAULT_CHARACTER_NAME,
        recordingUrl = audioUrl
            ?.takeIf { url -> url.isNotBlank() }
            ?: DEFAULT_RECORDING_URL,
        durationMillis = 0L,
    )

internal fun CallTranscriptDto.toDomain(): List<CallTranscript> =
    lines.map { line -> line.toDomain() }

private fun CallScriptMessage.toDomain(): CallTranscript =
    CallTranscript(
        content = content,
        speaker = speaker.toDomainSpeaker(),
        createdAt = TimeUtil.parseLocalDateTime(createdAt),
    )

private fun String?.toLocalDateTimeOrNow(): LocalDateTime =
    this
        ?.takeIf { value -> value.isNotBlank() }
        ?.let(TimeUtil::parseLocalDateTime)
        ?: LocalDateTime.now()

private fun String.toDomainSpeaker(): CallTranscript.Speaker =
    when (this) {
        "사용자", "USER" -> CallTranscript.Speaker.USER
        "AI" -> CallTranscript.Speaker.AI
        else -> error("지원하지 않는 화자입니다: $this")
    }

private const val DEFAULT_RECORDING_URL =
    "https://www.image2url.com/r2/default/audio/1785493840020-96ae33c3-41a8-4910-baf8-41ac5c674397.mp3"

private const val DEFAULT_CALL_TITLE = "통화 기록"

private const val DEFAULT_CHARACTER_NAME = "알 수 없는 상대"

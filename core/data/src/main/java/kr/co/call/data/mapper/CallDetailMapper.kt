package kr.co.call.data.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.network.dto.home.CallDetailDto
import java.time.LocalDateTime

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

private fun String?.toLocalDateTimeOrNow(): LocalDateTime =
    this
        ?.takeIf { value -> value.isNotBlank() }
        ?.let { value ->
            runCatching {
                TimeUtil.parseLocalDateTime(value)
            }.getOrNull()
        }
        ?: LocalDateTime.now()

private const val DEFAULT_RECORDING_URL =
    "https://www.image2url.com/r2/default/audio/1785493840020-96ae33c3-41a8-4910-baf8-41ac5c674397.mp3"

private const val DEFAULT_CALL_TITLE = "통화 기록"

private const val DEFAULT_CHARACTER_NAME = "알 수 없는 상대"

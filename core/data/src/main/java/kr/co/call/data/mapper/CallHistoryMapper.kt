package kr.co.call.data.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.network.dto.CallHistoryDto

internal fun CallHistoryDto.toDomain(): CallHistory {
    val parsedStartedAt = TimeUtil.parseLocalDateTime(startedAt)

    return CallHistory(
        callId = callId,
        characterName = characterName,
        aiSummary = aiSummary,
        startedAt = parsedStartedAt,
        isOutgoing = sender.toIsOutgoing(),
        isMissed = status.toIsMissed(),
    )
}

private fun String.toIsOutgoing(): Boolean =
    when (this) {
        "USER" -> true
        "AI" -> false
        else -> error("지원하지 않는 통화 발신자입니다: $this")
    }

private fun String.toIsMissed(): Boolean =
    when (this) {
        "COMPLETED" -> false
        "MISSED" -> true
        else -> error("지원하지 않는 통화 상태입니다: $this")
    }

package kr.co.call.data.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.model.home.NotificationType
import kr.co.call.network.dto.home.CallHistoryDto
import kr.co.call.network.dto.home.HomeNotificationDto
import kr.co.call.network.dto.home.HomeSummaryDto

internal fun HomeSummaryDto.toDomain(): HomeSummary =
    HomeSummary(
        firstName = firstName,
        relationshipDays = relationshipDays,
        totalCallCount = totalCallCount,
        callStreakDays = callStreakDays,
    )

internal fun CallHistoryDto.toDomain(): CallHistory =
    CallHistory(
        callId = callId,
        characterName = characterName,
        aiSummary = aiSummary.orEmpty(),
        startedAt = TimeUtil.parseLocalDateTime(createdAt),
        isOutgoing = sender.toIsOutgoing(),
        isMissed = status.toIsMissed(),
    )

private fun String.toIsOutgoing(): Boolean =
    when (this) {
        "USER" -> true
        "AI" -> false
        else -> error("지원하지 않는 통화 발신자입니다: $this")
    }

private fun String.toIsMissed(): Boolean =
    when (this) {
        "MISSED", "CANCELED" -> true
        else -> false
    }

internal fun HomeNotificationDto.toDomain(): HomeNotification =
    HomeNotification(
        notificationId = notificationId,
        type = NotificationType.valueOf(type),
        title = title,
        content = content,
        isRead = read,
        createdAt = TimeUtil.parseLocalDateTime(createdAt),
        characterId = characterId,
        characterName = characterName,
        profileImageUrl = characterImageUrl,
    )

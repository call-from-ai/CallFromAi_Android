package kr.co.call.data.mapper

import java.time.LocalDateTime
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallSender
import kr.co.call.domain.model.home.CallStatus
import kr.co.call.domain.model.home.CallType
import kr.co.call.domain.model.home.HomeModel
import kr.co.call.network.dto.CallHistoryDto
import kr.co.call.network.dto.HomeSummaryDto
import kr.co.call.network.dto.ReservationListDto
import kr.co.call.network.dto.ReservationDto

internal fun CallHistoryDto.toDomain(): CallHistory {
    val parsedStartedAt = TimeUtil.parseLocalDateTime(startedAt)

    return CallHistory(
        callId = callId,
        characterName = characterName,
        sender = CallSender.valueOf(sender),
        aiSummary = aiSummary,
        startedAt = parsedStartedAt,
        callType = CallType.valueOf(callType),
        status = CallStatus.valueOf(status),
    )
}

internal fun ReservationListDto.toDomain(): HomeModel.Reservations =
    HomeModel.Reservations(
        count = count,
        items = content.map { it.toDomain() },
    )

private fun ReservationDto.toDomain(): HomeModel.Reservation =
    HomeModel.Reservation(
        callReservationId = callReservationId,
        characterId = characterId,
        firstName = firstName,
        imageUrl = imageUrl,
        scheduledAt = LocalDateTime.parse(scheduledAt),
        status = HomeModel.ReservationStatus.valueOf(status),
    )

internal fun HomeSummaryDto.toDomain(characterId: Long): HomeModel.Summary =
    HomeModel.Summary(
        characterId = characterId,
        firstName = firstName,
        relationshipDays = relationshipDays,
        totalCallCount = totalCallCount,
        callStreakDays = callStreakDays,
    )

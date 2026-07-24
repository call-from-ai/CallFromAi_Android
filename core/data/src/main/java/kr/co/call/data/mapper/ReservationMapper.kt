package kr.co.call.data.mapper

import java.time.LocalDateTime
import kr.co.call.domain.model.home.CallReservation
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.network.dto.ReservationDto
import kr.co.call.network.dto.ReservationListDto

internal fun ReservationListDto.toDomain(): CallReservations =
    CallReservations(
        totalCount = count,
        items = content.map { reservation -> reservation.toDomain() },
    )

private fun ReservationDto.toDomain(): CallReservation =
    CallReservation(
        id = callReservationId,
        characterId = characterId,
        firstName = firstName,
        imageUrl = imageUrl,
        scheduledAt = LocalDateTime.parse(scheduledAt),
    )

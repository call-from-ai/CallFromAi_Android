package kr.co.call.impl.mapper

import kr.co.call.domain.model.home.HomeModel
import kr.co.call.impl.viewmodel.state.HomeReservationState
import kr.co.call.impl.viewmodel.state.HomeSummaryState

internal fun HomeModel.Summary.toUiState(): HomeSummaryState =
    HomeSummaryState(
        characterId = characterId,
        firstName = firstName,
        relationshipDays = relationshipDays,
        totalCallCount = totalCallCount,
        callStreakDays = callStreakDays,
    )

internal fun HomeModel.Reservations.toUiState(): HomeReservationState {
    val reservation = items.firstOrNull()

    return HomeReservationState(
        reservationCount = count,
        profileImageUrl = reservation?.imageUrl,
        scheduledAt = reservation?.scheduledAt,
        firstName = reservation?.firstName,
    )
}

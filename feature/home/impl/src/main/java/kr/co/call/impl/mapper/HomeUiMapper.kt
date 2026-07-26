package kr.co.call.impl.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.impl.viewmodel.model.HomeReservationUiModel
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

fun HomeSummary.toUiModel(): HomeSummaryUiModel =
    HomeSummaryUiModel(
        firstName = firstName,
        relationshipDaysText = relationshipDays.withSuffixOrDash("일째"),
        totalCallCountText = totalCallCount.withSuffixOrDash("회"),
        callStreakDaysText = callStreakDays.withSuffixOrDash("일"),
    )

fun CallReservations.toUiModel(): HomeReservationUiModel {
    val reservation = items.firstOrNull()

    return HomeReservationUiModel(
        hasReservation = reservation != null,
        reservationCountText = "약속 ${totalCount}건",
        profileImageUrl = reservation?.imageUrl,
        firstName = reservation?.firstName,
        scheduledAtText = reservation?.scheduledAt?.let(TimeUtil::toReservationTimeText),
        scheduledTimeText = reservation?.scheduledAt?.let(TimeUtil::toHourMinuteText),
    )
}

private fun Int.withSuffixOrDash(suffix: String): String =
    if (this == 0) "-" else "$this$suffix"

package kr.co.call.domain.model.home

/**
 * home에서 조회할 데이터
 */
data class HomeOverview(
    val summary: HomeSummary,
    val reservations: CallReservations,
    val callHistories: List<CallHistory>,
)

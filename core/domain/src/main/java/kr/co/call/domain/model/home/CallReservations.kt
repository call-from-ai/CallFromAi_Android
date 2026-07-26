package kr.co.call.domain.model.home

data class CallReservations(
    val totalCount: Int,
    val items: List<CallReservation>,
)

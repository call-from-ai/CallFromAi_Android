package kr.co.call.impl.viewmodel.model

data class HomeReservationUiModel(
    val reservationId: Long? = null,
    val hasReservation: Boolean = false,
    val reservationCountText: String = "약속 0건",
    val profileImageUrl: String? = null,
    val firstName: String? = null,
    val scheduledAtText: String? = null,
    val scheduledTimeText: String? = null,
)

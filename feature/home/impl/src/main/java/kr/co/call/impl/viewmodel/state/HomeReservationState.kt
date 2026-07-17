package kr.co.call.impl.viewmodel.state

import java.time.LocalDateTime

/**
 * 전화 예약 상태
 */
data class HomeReservationState(
    val reservationCount: Int = 0,
    val profileImageUrl: String? = "",
    val scheduledAt: LocalDateTime? = LocalDateTime.of(2026, 6, 30, 21, 0),
    val firstName: String? = "민준",
)
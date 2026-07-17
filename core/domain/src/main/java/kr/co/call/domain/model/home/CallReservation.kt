package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class CallReservation(
    val id: Long,
    val characterId: Long,
    val firstName: String,
    val imageUrl: String?,
    val scheduledAt: LocalDateTime,
    val status: ReservationStatus,
)

enum class ReservationStatus {
    SCHEDULED,
}

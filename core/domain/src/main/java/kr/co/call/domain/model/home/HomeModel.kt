package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class HomeModel(
    val summary: Summary,
    val reservations: Reservations,
    val callHistories: List<CallHistory>,
) {
    data class Summary(
        val characterId: Long,
        val firstName: String,
        val relationshipDays: Int,
        val totalCallCount: Int,
        val callStreakDays: Int,
    )

    data class Reservations(
        val count: Int,
        val items: List<Reservation>,
    )

    data class Reservation(
        val callReservationId: Long,
        val characterId: Long,
        val firstName: String,
        val imageUrl: String?,
        val scheduledAt: LocalDateTime,
        val status: ReservationStatus,
    )

    enum class ReservationStatus {
        SCHEDULED,
    }

    data class CallHistory(
        val callId: Long,
        val characterName: String,
        val aiSummary: String,
        val startedAt: LocalDateTime,
        val sender: CallSender,
        val callType: CallType,
        val status: CallStatus,
    )

}

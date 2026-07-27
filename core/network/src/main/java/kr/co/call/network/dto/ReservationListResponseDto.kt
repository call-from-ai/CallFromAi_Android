package kr.co.call.network.dto

data class ReservationListResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ReservationListDto,
)

data class ReservationListDto(
    val content: List<ReservationDto>,
    val count: Int,
)

data class ReservationDto(
    val callReservationId: Long,
    val characterId: Long,
    val firstName: String,
    val imageUrl: String?,
    val scheduledAt: String,
    val status: String,
)

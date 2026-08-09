package kr.co.call.network.dto.call

/**
 * 웹소켓 DTO
 */
data class CallWebSocketMessageDto(
    val type: String,
    val data: CallWebSocketDataDto?,
)

/**
 * 웹소켓 데이터 DTO
 */
data class CallWebSocketDataDto(
    val callId: Long?,
    val reason: String?,
    val callTime: Long?,
)

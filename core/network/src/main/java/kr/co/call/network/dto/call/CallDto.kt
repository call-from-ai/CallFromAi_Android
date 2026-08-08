package kr.co.call.network.dto.call

/**
 * 통화 발신 요청 DTO
 */
data class DialCallRequestDto(
    val characterId: Long,
)

/**
 * 종료된 통화 목록 응답 DTO
 */
data class CallListResultDto(
    val content: List<CallListItemDto>,
)

/**
 * 종료된 통화 한 건의 목록 정보 DTO
 */
data class CallListItemDto(
    val callId: Long,
    val characterName: String,
    val sender: String,
    val aiSummary: String?,
    val createdAt: String,
    val status: String,
)

/**
 * 통화 기록 상세 응답 DTO
 */
data class CallDetailResultDto(
    val characterName: String,
    val aiSummary: String?,
    val summaryStatus: String,
    val createdAt: String,
    val audioUrl: String?,
    val recordingStatus: String,
)

/**
 * 통화 전사 응답 DTO
 */
data class CallScriptResultDto(
    val callId: Long,
    val lines: List<CallScriptLineDto>,
)

/**
 * 통화 전사의 발화 한 줄 DTO
 */
data class CallScriptLineDto(
    val speaker: String,
    val content: String,
    val createdAt: String,
)

/**
 * 현재 착신 대기 중인 통화 DTO
 */
data class IncomingCallResultDto(
    val callId: Long,
    val characterId: Long,
    val characterName: String,
    val characterImage: String?,
    val createdAt: String,
)

/**
 * 통화 수락 또는 발신 결과 DTO
 */
data class CallConnectionResultDto(
    val callId: Long,
    val callStatus: String,
    val wsTicket: String,
)

/**
 * 통화 종료 결과 DTO
 */
data class CallEndResultDto(
    val callId: Long,
    val callTime: Long,
    val endedAt: String,
)


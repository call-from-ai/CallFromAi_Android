package kr.co.call.domain.model.call

import java.time.LocalDateTime

/**
 * 통화 목록에 표시할 종료 통화 정보
 */
data class CallListItem(
    val callId: Long,
    val characterName: String,
    val sender: CallSender,
    val aiSummary: String,
    val createdAt: LocalDateTime,
    val status: CallStatus,
)

/**
 * 통화 발신자 구분
 */
enum class CallSender {
    USER,
    AI,
    UNKNOWN,
}

/**
 * 서버 통화 상태
 */
enum class CallStatus {
    RINGING,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELED,
    MISSED,
    REJECTED,
    UNKNOWN,
}

/**
 * 통화 수락 또는 발신 후 WebSocket 연결에 필요한 정보
 */
data class CallConnectionInfo(
    val callId: Long,
    val callStatus: CallStatus,
    val wsTicket: String,
)

/**
 * 통화 종료 결과입니다.
 */
data class CallEndInfo(
    val callId: Long,
    val callTime: Long,
    val endedAt: LocalDateTime,
)

/**
 * 현재 활성화된(메인) 캐릭터 표시 정보
 * 통화는 항상 메인 캐릭터에게만 걸 수 있어, 발신 화면에 보여줄 이름·사진 조회에 사용
 */
data class ActiveCharacter(
    val characterId: Long,
    val name: String,
    val imageUrl: String?,
)

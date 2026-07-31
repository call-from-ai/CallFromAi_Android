package kr.co.call.domain.model.chatting

/**
 * 채팅 도메인 내에서 발생하는 다양한 비즈니스 사건(Event)을 정의하는 봉인된 인터페이스입니다.
 *
 * UI 레이어에서의 낙관적 업데이트(Optimistic Update)나 도메인 상태 변경 알림 등
 * 채팅 흐름 제어를 위한 이벤트 객체들을 포함합니다.
 *
 * 추후 SSE 연동 시 connect / message / heartbeat 이벤트에 대응하는 하위 타입이 추가될 예정입니다.
 */
sealed interface ChatEvent {
    // 유저가 메시지 전송 성공 시 (낙관적 업데이트용)
    data class MessageSent(
        val roomId: Long,
        val content: String,
        val whenSubmitted: String,
    ) : ChatEvent
}

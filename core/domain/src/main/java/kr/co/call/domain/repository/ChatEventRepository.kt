package kr.co.call.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kr.co.call.domain.model.chatting.ChatEvent
import java.time.LocalDateTime

/**
 * 채팅 이벤트를 앱 내부에 전파하는 이벤트 버스 역할의 Repository입니다.
 *
 * 현재는 메시지 전송 성공 시 낙관적 업데이트를 위한 로컬 emit만 지원합니다.
 *
 * ## 추후 SSE 연동 계획
 * 서버의 `GET /chat-rooms/subscribe` SSE 엔드포인트와 연결되면
 * 아래 이벤트들이 [events]를 통해 추가로 흘러옵니다:
 * - `event: connect` → 연결 수립 확인
 * - `event: message` → 수신된 채팅 메시지 ([ChatEvent] 하위 타입으로 확장 예정)
 * - `event: heartbeat` → 연결 유지 신호
 *
 * SSE 연결/해제 메서드가 추가될 예정이며, 소비자는 [events]만 구독하면
 * 낙관적 업데이트와 SSE 수신 메시지를 동일한 파이프라인으로 처리할 수 있습니다.
 */
interface ChatEventRepository {
    // 채팅 이벤트 스트림. 추후 SSE 수신 이벤트도 이 Flow를 통해 전달될 예정
    val events: SharedFlow<ChatEvent>

    // 메시지 전송 성공 시 낙관적 업데이트용 이벤트 emit
    fun emitMessageSent(roomId: Long, content: String, createdTime: LocalDateTime)
}

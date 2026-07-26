package kr.co.call.impl.model

import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType

/**
 * 채팅 말풍선 하나의 UI 모델.
 *
 * 서버에서 받은 메시지와 아직 전송 중인 로컬 메시지를 모두 표현한다.
 *
 * @property clientId 로컬에서 부여하는 고정 식별자. LazyColumn의 item key이자
 *   전송 요청~서버 응답 매칭에 사용한다. 서버 메시지는 id 기반으로, 로컬 메시지는 UUID로 생성한다.
 * @property chatMessageId 서버가 부여한 실제 메시지 id. 전송 완료 전(SENDING/FAILED)에는 null.
 * @property senderType 발신 주체. UI 좌우 정렬에 사용한다(AI=왼쪽, USER=오른쪽).
 * @property content 메시지 텍스트. 이미지 전용 메시지면 빈 문자열일 수 있다.
 * @property messageType 메시지 종류(TEXT / IMAGE / TEXT_IMAGE).
 * @property photoUrl 첨부 이미지 URL. 없으면 null.
 * @property time 표시용으로 포맷된 시간 문자열(예: "13:00").
 * @property sendStatus 전송 상태. 서버에서 받은 메시지는 항상 SENT.
 */
data class ChatItemUiModel(
    val clientId: String,
    val chatMessageId: Long? = null,
    val senderType: SenderType,
    val content: String,
    val messageType: MessageType,
    val photoUrl: String? = null,
    val time: String = "",
    val sendStatus: SendStatus = SendStatus.SENT,
)

enum class SendStatus {
    SENDING,
    SENT,
    FAILED,
}

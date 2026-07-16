package kr.co.call.domain.model.chatting

/**
 * 채팅 목록에서 표시될 채팅 항목의 요약 정보를 나타냅니다.
 *
 * @property image 채팅 참여자의 프로필 이미지 URL 또는 경로.
 * @property name 채팅 상대의 이름.
 * @property isMainCharacter 해당 상대가 메인 캐릭터인지 여부.
 * @property content 최신 메시지의 실제 내용 또는 대화 내용의 일부.
 * @property whenSubmitted 마지막 메시지가 전송된 시각.
 * @property unReadMessageCount 안읽은 메세지 수
 *
 *  */
data class ChatSummary(
    val image: String,
    val name: String,
    val isMainCharacter: Boolean,
    val content: String,
    val whenSubmitted: String,
    val unReadMessageCount: String,
    val isAlarmEnabled: Boolean
)

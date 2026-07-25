package kr.co.call.domain.model.chatting

import java.time.LocalDateTime

/**
 * 매니저와의 채팅 화면에서 표시되는 각 채팅 아이템의 공통 규격을 정의하는 봉인 인터페이스(Sealed Interface)입니다.
 *
 * 이 인터페이스는 매니저의 안내 메시지, 시스템 공지성 메시지, 그리고 사용자의 메시지를 모두 포함합니다.
 *
 * @property id 각 채팅 아이템을 식별하기 위한 고유 식별자
 * @property content 채팅 창에 표시될 메시지 본문 내용
 * @property createdAt 메시지가 생성된 일시
 */
sealed interface ManagerChatItem {
    val id: String
    val content: String
    val createdAt: LocalDateTime
}

// ======== 첫 번째 매니저 채팅 ==========
data class ManagerFirstMessage(
    override val id: String,
    override val content: String,
    val type: ManagerFirstMessageType,
    override val createdAt: LocalDateTime,
): ManagerChatItem

enum class ManagerFirstMessageType {
    NORMAL,
    RELATIONSHIP // 대화를 많이 나눌수록, 더 자연스러운 관계가 생성돼요.
}


// ======= 전화는 언제 오나요? 에 대한 매니저 채팅 ==========
data class WhenCallMessage(
    override val id: String,
    override val content: String,
    override val createdAt: LocalDateTime,
): ManagerChatItem


// ======= 상대방 정보를 변경하고 싶어요. 에 대한 매니저 채팅 ==========
data class ChangePartnerInfoMessage(
    override val id: String,
    override val content: String,
    val type: ChangePartnerInfoMessageType,
    override val createdAt: LocalDateTime,
): ManagerChatItem

enum class ChangePartnerInfoMessageType {
    NORMAL,
    NEXT_CONVERSATION // 변경한 설정은 다음 대화와 전화부터 바로 반영 돼요.
}


// ======= 기록한 정보를 수정하고 싶어요. 에 대한 매니저 채팅 ==========
data class UpdateInfoMessage(
    override val id: String,
    override val content: String,
    val type: UpdateInfoMessageType,
    override val createdAt: LocalDateTime,
): ManagerChatItem

enum class UpdateInfoMessageType {
    NORMAL,
    APPLIED_NATURALLY // 수정한 정보는 이후 대화에 자연스럽게 반영 돼요 !
}


// ======= 상담원에게 문의하기 에 대한 매니저 채팅 ==========
data class AskToAgentMessage(
    override val id: String,
    override val content: String,
    override val createdAt: LocalDateTime,
): ManagerChatItem


// ======== 유저 채팅 =========
data class UserMessage(
    override val id: String,
    override val content: String,
    override val createdAt: LocalDateTime,
): ManagerChatItem

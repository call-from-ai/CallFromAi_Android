package kr.co.call.domain.model.chatting

/**
 * 채팅방의 상단 정보나 목록 화면에서 보여줄 헤더 데이터를 관리하는 모델입니다.
 */
data class ChatHeader(
    val characterId: Long,
    val characterFirstName: String = "",
    val characterImageUrl: String = "",
    val dDay: Int = 1,
)

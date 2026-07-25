package kr.co.call.domain.model.chatting


/**
 * 채팅방의 상단 정보나 목록 화면에서 보여줄 헤더 데이터를 관리하는 모델 클래스입니다.
 *
 * @property characterId 캐릭터의 고유 식별자.
 * @property characterFirstName 캐릭터의 이름.
 * @property characterImageUrl 캐릭터 프로필 이미지의 URL 경로.
 * @property dDay 캐릭터와 대화를 시작한 지 경과된 일수 또는 특정 기준일로부터의 디데이.
 */
data class ChatHeader(
    val characterId: Long,
    val characterFirstName: String = "",
    val characterImageUrl: String = "",
    val dDay: Int = 1,
)

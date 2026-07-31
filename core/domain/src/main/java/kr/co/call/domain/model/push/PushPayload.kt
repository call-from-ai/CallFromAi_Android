package kr.co.call.domain.model.push

/**
 * FCM data 페이로드 수신 계약
 *
 * - CHAT: 배너 + chatRoomId -> 채팅방 이동
 * - CALL: data-only 커스텀 착신 UI
 * - NOTICE: 일반 배너
 *
 * data 값은 FCM 규격상 모두 문자열이다. 파싱은 [PushPayloadParser]가 담당한다.
 */
sealed interface PushPayload {

    /**
     * 채팅 알림. 클릭 시 [chatRoomId] 방으로 이동한다.
     * title/body는 OS notification 또는 data에서 올 수 있다.
     */
    data class Chat(
        val chatRoomId: Long,
        val title: String,
        val body: String,
    ) : PushPayload

    /**
     * 전화 착신. 배너 없이 data-only로 오며 수락/거절/채팅 이동 UI를 그린다.
     */
    data class Call(
        val callId: Long,
        val characterId: Long,
        val characterName: String,
        val characterImageUrl: String,
        val chatRoomId: Long,
    ) : PushPayload

    /**
     * 공지/일반 알림 배너
     */
    data class Notice(
        val title: String,
        val body: String,
    ) : PushPayload
}

/**
 * FCM data 맵 키
 */
object PushDataKeys {
    const val TYPE = "type"
    const val CHAT_ROOM_ID = "chatRoomId"
    const val CALL_ID = "callId"
    const val CHARACTER_ID = "characterId"
    const val CHARACTER_NAME = "characterName"
    const val CHARACTER_IMAGE_URL = "characterImageUrl"
}

/**
 * data["type"] 값. BE [PushType] enum name과 동일
 */
enum class PushType {
    CHAT,
    CALL,
    NOTICE,
    ;

    companion object {
        fun fromDataValue(value: String?): PushType? =
            entries.find { it.name == value }
    }
}

object PushChannels {
    const val GENERAL = "general"
}

package kr.co.call.domain.model.push

/**
 * FCM 수신 계약
 *
 * - CHAT: notification(title/body) + data(type, chatRoomId) 클릭 시 해당 채팅방 이동
 * - CALL: data-only(type, callId, characterId, characterName, characterImageUrl, chatRoomId)
 *   수락/거절/채팅 이동 커스텀 착신 UI
 * - NOTICE: notification(title/body) + data(type)
 *
 * data 값은 FCM 규격상 모두 문자열이다. 파싱은 [PushPayloadParser]가 담당한다.
 */
sealed interface PushPayload {

    /**
     * 채팅 알림. 클릭 시 [chatRoomId] 방으로 이동한다.
     * title/body는 RemoteMessage.notification 만 사용한다 (data에 title/body 없음)
     * data에는 type, chatRoomId 만 온다.
     */
    data class Chat(
        val chatRoomId: Long,
        val title: String,
        val body: String,
    ) : PushPayload

    /**
     * 전화 착신. notification 없이 data-only 로 오며
     * 수락/거절/채팅 이동 커스텀 UI를 그린다.
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
     * title/body는 RemoteMessage.notification 만 사용한다 (data에는 type 만)
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
 * data["type"] 값
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

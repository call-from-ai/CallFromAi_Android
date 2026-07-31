package kr.co.call.domain.model.push

/**
 * FCM RemoteMessage의 data를 [PushPayload]로 변환한다.
 *
 * Android SDK 의존 없이 [Map]만 받으므로 domain에 둔다.
 * 필수 필드 누락/type 미지/숫자 파싱 실패 시 null
 */
object PushPayloadParser {

    /**
     * @param data FCM data 맵 (값은 모두 문자열)
     * @param notificationTitle OS notification title (CHAT/NOTICE, 없으면 null)
     * @param notificationBody OS notification body (CHAT/NOTICE, 없으면 null)
     */
    fun parse(
        data: Map<String, String>,
        notificationTitle: String? = null,
        notificationBody: String? = null,
    ): PushPayload? {
        return when (PushType.fromDataValue(data[PushDataKeys.TYPE])) {
            PushType.CHAT -> parseChat(data, notificationTitle, notificationBody)
            PushType.CALL -> parseCall(data)
            PushType.NOTICE -> parseNotice(notificationTitle, notificationBody)
            null -> null
        }
    }

    private fun parseChat(
        data: Map<String, String>,
        notificationTitle: String?,
        notificationBody: String?,
    ): PushPayload.Chat? {
        val chatRoomId = data[PushDataKeys.CHAT_ROOM_ID]?.toLongOrNull() ?: return null
        return PushPayload.Chat(
            chatRoomId = chatRoomId,
            title = notificationTitle.orEmpty(),
            body = notificationBody.orEmpty(),
        )
    }

    private fun parseCall(data: Map<String, String>): PushPayload.Call? {
        val callId = data[PushDataKeys.CALL_ID]?.toLongOrNull() ?: return null
        val characterId = data[PushDataKeys.CHARACTER_ID]?.toLongOrNull() ?: return null
        val chatRoomId = data[PushDataKeys.CHAT_ROOM_ID]?.toLongOrNull() ?: return null
        val characterName = data[PushDataKeys.CHARACTER_NAME] ?: return null
        val characterImageUrl = data[PushDataKeys.CHARACTER_IMAGE_URL] ?: return null

        return PushPayload.Call(
            callId = callId,
            characterId = characterId,
            characterName = characterName,
            characterImageUrl = characterImageUrl,
            chatRoomId = chatRoomId,
        )
    }

    private fun parseNotice(
        notificationTitle: String?,
        notificationBody: String?,
    ): PushPayload.Notice {
        return PushPayload.Notice(
            title = notificationTitle.orEmpty(),
            body = notificationBody.orEmpty(),
        )
    }
}

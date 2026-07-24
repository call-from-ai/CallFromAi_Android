package kr.co.call.impl.intent

import android.net.Uri

sealed interface ChatRoomIntent {

    data class SendMessage(
        val roomId: Long,
        val message: String? = null,
        val imageUri: Uri? = null,
    ): ChatRoomIntent

    data class ClickCall(
        val characterId: Long,
    ): ChatRoomIntent

    data object ClickCamera: ChatRoomIntent

    data object ClickGallery: ChatRoomIntent

    data class LongPressMessage(
        val messageId: Long,
    ): ChatRoomIntent


}
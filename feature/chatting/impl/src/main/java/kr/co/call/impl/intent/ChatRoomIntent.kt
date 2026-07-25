package kr.co.call.impl.intent

import android.net.Uri
import kr.co.call.domain.model.chatting.ImageData

sealed interface ChatRoomIntent {

    data class SendMessage(
        val message: String? = null,
        val image: ImageData? = null,
    ) : ChatRoomIntent

    data class ClickCall(
        val characterId: Long,
    ): ChatRoomIntent

    data class GoToCall(
        val characterId: Long,
    ): ChatRoomIntent

    data object ClickCamera: ChatRoomIntent

    data object ClickGallery: ChatRoomIntent

    data class LongPressMessage(
        val messageId: Long,
    ): ChatRoomIntent

    data class DeleteMessage(
        val messageId: Long
    ): ChatRoomIntent

    data class ImagesPicked(val uri: Uri) : ChatRoomIntent // 갤러리

    data class PictureTaken(val uri: Uri) : ChatRoomIntent // 카메라

    data object CancelImage: ChatRoomIntent


}
package kr.co.call.impl.viewmodel

import android.net.Uri
import kr.co.call.domain.model.chatting.ChatMessageList
import kr.co.call.domain.util.LoadStatus

data class ChatRoomUiState(
    val messages: ChatMessageList = ChatMessageList(emptyList(), null, false),
    val initialLoadStatus: LoadStatus = LoadStatus.Idle,
    val textFieldState: TextFieldState = TextFieldState(),
    val sendMessageState: SendMessageState = SendMessageState()
)

data class TextFieldState(

    val text: String = "",
    val selectedImage: Uri? = null,
    val loadStatus: LoadStatus = LoadStatus.Idle // 이미지 업로드 시 로딩 상태
)

data class SendMessageState(
    val text: String = "",
    val image: Uri? = null,
    val sendStatus: SendStatus = SendStatus.IDLE
)


// 유저가 메세지를 전송하고 전송중인지 여부를 나타내는 enum
enum class SendStatus {
    IDLE,
    SENDING,
    SENT,
    FAILED
}


package kr.co.call.impl.state

import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.util.LoadStatus

data class ChatListState(
    val chatList: List<ChatSummary> = emptyList(),
    val status: LoadStatus = LoadStatus.Idle,
    val showDeleteChatRoomDialog: Boolean = false,
    val deleteTargetRoomId: Long? = null,
)
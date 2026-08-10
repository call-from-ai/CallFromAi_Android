package kr.co.call.callfromai.incomingchat

data class IncomingChat(
    val chatRoomId: Long,
    val characterName: String,
    val message: String,
    val profileImageUrl: String? = null,
)
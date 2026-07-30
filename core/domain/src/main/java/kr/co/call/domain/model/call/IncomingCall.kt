package kr.co.call.domain.model.call

data class IncomingCall(
    val callId: Long,
    val characterId: Long,
    val characterName: String,
    val characterImage: String?,
    val chatRoomId: Long?,
)

package kr.co.call.domain.model.call

// 통화 걸려올 때 FCM에서 받아오는 데이터
data class IncomingCall(
    val callId: Long,
    val characterId: Long,
    val characterName: String,
    val characterImageUrl: String?,
    val chatRoomId: Long?,
)

package kr.co.call.domain.model.mypage

data class MyPageProfile(
    val profileImageUrl: String,
    val nickname: String,
    val tier: String,
    val remainingTicketCount: Int,
    val appVersion: String,
)

package kr.co.call.network.dto.mypage

data class MemberResponseDto(
    val memberId: Long,
    val lastName: String? = null,
    val firstName: String? = null,
    val profilePhotoUrl: String? = null,
    val gender: String? = null,
    val birth: String? = null,
    val mbti: String? = null,
    val job: String? = null,
    val socialType: String? = null,
    val callTicketBalance: Int = 0,
    val needsOnboarding: Boolean = false,
)

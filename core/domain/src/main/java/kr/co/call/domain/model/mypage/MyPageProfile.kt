package kr.co.call.domain.model.mypage

/**
 * 마이페이지/프로필/내 정보 수정에서 공유하는 회원 프로필
 */
data class MyPageProfile(
    val profileImageUrl: String = "",
    val nickname: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val gender: String? = null,
    val birth: String? = null,
    val mbti: String? = null,
    val job: String? = null,
    val tier: String = "",
    val remainingTicketCount: Int = 0,
    val appVersion: String = "",
)

/**
 * 내 정보 부분 수정 요청
 */
data class MemberProfileUpdate(
    val lastName: String? = null,
    val firstName: String? = null,
    val imageUrl: String? = null,
    val gender: String? = null,
    val birth: String? = null,
    val mbti: String? = null,
    val job: String? = null,
)

package kr.co.call.network.dto.onboarding

data class UpdateMemberRequestDto(
    val lastName: String? = null,
    val firstName: String? = null,
    val imageUrl: String? = null,
    val gender: String? = null,
    val birth: String? = null,
    val mbti: String? = null,
    val job: String? = null,
)

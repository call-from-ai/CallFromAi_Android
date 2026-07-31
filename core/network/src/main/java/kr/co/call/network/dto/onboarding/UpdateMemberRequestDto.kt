package kr.co.call.network.dto.onboarding

data class UpdateMemberRequestDto(
    val lastName: String,
    val firstName: String,
    val imageUrl: String,
    val gender: String,
    val birth: String,
    val mbti: String,
    val job: String,
)
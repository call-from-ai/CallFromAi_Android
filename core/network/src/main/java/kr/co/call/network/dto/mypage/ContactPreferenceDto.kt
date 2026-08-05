package kr.co.call.network.dto.mypage

data class ContactPreferenceUpdateRequestDto(
    val preferTime: String,
)

data class ContactPreferenceResponseDto(
    val preferTime: String? = null,
)

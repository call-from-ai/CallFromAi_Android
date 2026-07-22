package kr.co.call.network.dto.login

data class LoginRequestDto(
    val provider: String="KAKAO",
    val accessToken: String,
)
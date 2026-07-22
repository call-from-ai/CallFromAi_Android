package kr.co.call.network.dto.login

//토큰 재발급 요청
data class TokenReissueRequestDto(
    val accessToken: String,
    val refreshToken: String,
)

//토큰 재발급 응답
data class TokenReissueResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginTokenResult
)
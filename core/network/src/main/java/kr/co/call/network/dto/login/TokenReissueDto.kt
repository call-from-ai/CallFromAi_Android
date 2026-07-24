package kr.co.call.network.dto.login

/**
 * 만료된 Access Token을 새 토큰으로 교체하기 위한 재발급 요청 DTO
 * 기존 Access Token과 Refresh Token을 서버에 함께 전달한다.
 */
data class TokenReissueRequestDto(
    val accessToken: String,
    val refreshToken: String,
)

/**
 * 토큰 재발급 API의 응답을 담는 DTO
 * 성공하면 result에 새 Access Token과 Refresh Token이 포함된다.
 */
data class TokenReissueResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginTokenResult?,
)
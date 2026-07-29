package kr.co.call.network.dto.login

/**
 * 만료된 Access Token을 새 토큰으로 교체하기 위한 재발급 요청 DTO
 * 기존 Access Token과 Refresh Token을 서버에 함께 전달한다.
 */
data class TokenReissueRequestDto(
    val refreshToken: String,
)
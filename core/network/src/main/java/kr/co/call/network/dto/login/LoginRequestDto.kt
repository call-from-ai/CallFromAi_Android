package kr.co.call.network.dto.login

/**
 * 카카오 로그인 정보를 서버에 전달하기 위한 요청 DTO
 * 카카오 SDK에서 받은 Access Token과 로그인 제공자를 서버로 전송한다.
 */
data class LoginRequestDto(
    val provider: String="KAKAO",
    val accessToken: String,
)
package kr.co.call.network.dto.login

/**
 * 서버 로그인 API의 전체 응답을 담는 DTO
 * 로그인 성공 시 result에 서비스용 Access Token과 Refresh Token이 포함된다.
 */
data class LoginResponseDto (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginTokenResult?,
)

/**
 * 서버에서 발급한 서비스 인증 토큰을 담는 객체
 * Access Token은 API 인증에, Refresh Token은 토큰 재발급에 사용한다.
 */
data class LoginTokenResult(
    val accessToken: String,
    val refreshToken: String,
)
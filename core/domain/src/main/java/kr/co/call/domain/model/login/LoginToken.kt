package kr.co.call.domain.model.login

/**
 * 로그인 성공 후 발급받은 서비스 인증 토큰을 나타내는 Domain 모델
 * Network DTO에 직접 의존하지 않고 앱 내부에서 토큰 값을 전달할 때 사용한다.
 */
data class LoginToken(
    val accessToken: String,
    val refreshToken: String,
)
package kr.co.call.network.dto.login

/**
 * 서버에서 발급한 서비스 인증 토큰을 담는 객체
 * Access Token은 API 인증에, Refresh Token은 토큰 재발급에 사용한다.
 */
data class LoginTokenResult(
    val accessToken: String,
    val refreshToken: String,
    val needsOnboarding: Boolean,
    val needsTermsAgreement: Boolean,
)
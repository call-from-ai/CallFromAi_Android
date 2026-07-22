package kr.co.call.domain.model.login

data class LoginToken(
    val accessToken: String,
    val refreshToken: String,
)
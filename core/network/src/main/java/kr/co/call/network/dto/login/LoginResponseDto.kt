package kr.co.call.network.dto.login

data class LoginResponseDto (
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginTokenResult?,
)

data class LoginTokenResult(
    val accessToken: String,
    val refreshToken: String,
)
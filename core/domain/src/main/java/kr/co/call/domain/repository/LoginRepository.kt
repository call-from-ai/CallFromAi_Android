package kr.co.call.domain.repository

import kr.co.call.domain.model.login.LoginToken

interface LoginRepository {
    suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): LoginToken
}
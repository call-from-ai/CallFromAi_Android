package kr.co.call.domain.repository

import kr.co.call.domain.model.login.LoginToken

/**
 * 로그인 기능에 대한 Repository 인터페이스
 * ViewModel이 Retrofit이나 DataStore 구현을 직접 알지 않도록 역할을 분리한다.
 */
interface LoginRepository {
    suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): LoginToken
}
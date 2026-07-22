package kr.co.call.data.repositoryImpl

import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.login.LoginToken
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.LoginRequestDto
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val tokenDataStore: TokenDataStore,
) : LoginRepository {

    override suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): LoginToken {
        val response = loginApi.login(
            request = LoginRequestDto(
                provider = "KAKAO",
                accessToken = kakaoAccessToken,
            ),
        )

        if (!response.isSuccess) {
            throw IllegalStateException(response.message)
        }

        val result = response.result
            ?: throw IllegalStateException("로그인 응답에 토큰 정보가 없습니다.")

        //서버가 내려준 토큰을 로컬에 저장
        tokenDataStore.saveTokens(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
        )

        return LoginToken(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
        )
    }
}
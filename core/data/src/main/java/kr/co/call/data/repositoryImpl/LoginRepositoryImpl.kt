package kr.co.call.data.repositoryImpl

import kr.co.call.domain.model.login.LoginToken
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.LoginRequestDto
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
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
            ?: throw IllegalStateException(
                "로그인 응답에 토큰 정보가 없습니다.",
            )

        return LoginToken(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
        )
    }
}
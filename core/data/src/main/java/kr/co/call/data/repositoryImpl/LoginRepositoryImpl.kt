package kr.co.call.data.repositoryImpl

import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.login.LoginToken
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.LoginRequestDto
import javax.inject.Inject

/**
 * 카카오 로그인 서버 통신을 실제로 수행하는 Repository 구현체
 * 서버 로그인 성공 후 발급받은 Access Token과 Refresh Token을 DataStore에 저장한다.
 */
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val tokenDataStore: TokenDataStore,
) : LoginRepository {

    override suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): LoginToken {
        // 카카오 SDK에서 받은 토큰을 서버 로그인 API에 전달
        val response = loginApi.login(
            request = LoginRequestDto(
                provider = "KAKAO",
                accessToken = kakaoAccessToken,
            ),
        )

        // 서버에서 로그인 실패 응답을 반환하면 ViewModel로 예외 전달
        if (!response.isSuccess) {
            throw IllegalStateException(response.message)
        }

        // 성공 응답이지만 토큰 정보가 없는 비정상적인 경우 예외 처리
        val result = response.result
            ?: throw IllegalStateException("로그인 응답에 토큰 정보가 없습니다.")

        /**
         * 서버에서 발급한 서비스용 토큰을 DataStore에 저장한다.
         * 이후 API 인증, 자동 로그인, 토큰 재발급에 사용된다.
         */
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
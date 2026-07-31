package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.push.PushTokenManager
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.login.LoginToken
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.LoginRequestDto
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall

/**
 * 카카오 로그인 서버 통신을 실제로 수행하는 Repository 구현체입니다.
 * 서버 로그인 성공 후 발급받은 Access Token과 Refresh Token을 DataStore에 저장합니다.
 */
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val tokenDataStore: TokenDataStore,
    private val errorResponseParser: ErrorResponseParser,
    private val pushTokenManager: PushTokenManager,
) : LoginRepository {

    /**
     * 카카오 SDK에서 발급받은 토큰을 서버에 전달해 서비스용 토큰을 발급받습니다.
     * 로그인 성공 여부와 실패 예외를 Result 형태로 ViewModel에 전달합니다.
     * JWT 저장 후 FCM 토큰 서버 등록을 시도한다(실패해도 로그인은 성공)
     */
    override suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): Result<LoginToken> =
        runRepositoryCatching {
            val result = safeApiCall(errorResponseParser) {
                loginApi.login(
                    request = LoginRequestDto(
                        kakaoAccessToken = kakaoAccessToken,
                    ),
                )
            }
            require(
                result.accessToken.isNotBlank() && result.refreshToken.isNotBlank(),
            ) {
                "로그인 응답 토큰 값이 비어 있습니다"
            }
            tokenDataStore.saveTokens(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            )

            // AuthInterceptor 가 JWT 를 쓸 수 있는 상태에서 등록
            pushTokenManager.registerCurrentDevice()

            LoginToken(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                needsOnboarding = result.needsOnboarding,
                needsTermsAgreement = result.needsTermsAgreement,
            )
        }
}
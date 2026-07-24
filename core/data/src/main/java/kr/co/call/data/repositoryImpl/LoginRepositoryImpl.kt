package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.login.LoginToken
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.LoginRequestDto

/**
 * 카카오 로그인 서버 통신을 실제로 수행하는 Repository 구현체입니다.
 * 서버 로그인 성공 후 발급받은 Access Token과 Refresh Token을 DataStore에 저장합니다.
 */
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val tokenDataStore: TokenDataStore,
) : LoginRepository {

    /**
     * 카카오 SDK에서 발급받은 토큰을 서버에 전달해 서비스용 토큰을 발급받습니다.
     * 로그인 성공 여부와 실패 예외를 Result 형태로 ViewModel에 전달합니다.
     */
    override suspend fun loginWithKakao(
        kakaoAccessToken: String,
    ): Result<LoginToken> {
        return try {
            // 카카오 SDK에서 받은 토큰을 서버 로그인 API에 전달
            val response = loginApi.login(
                request = LoginRequestDto(
                    provider = "KAKAO",
                    accessToken = kakaoAccessToken,
                ),
            )

            // 서버가 로그인 실패 응답을 반환한 경우
            if (!response.isSuccess) {
                throw IllegalStateException(
                    response.message.ifBlank {
                        "로그인에 실패했습니다."
                    },
                )
            }

            // 성공 응답이지만 토큰 정보가 없는 비정상적인 경우
            val result = response.result
                ?: throw IllegalStateException(
                    "로그인 응답에 토큰 정보가 없습니다.",
                )

            // 빈 Access Token이나 Refresh Token이 저장되는 것을 방지
            if (
                result.accessToken.isBlank() ||
                result.refreshToken.isBlank()
            ) {
                throw IllegalStateException(
                    "로그인 응답의 토큰 값이 비어 있습니다.",
                )
            }

            /**
             * 서버에서 발급한 서비스용 토큰을 DataStore에 저장합니다.
             * 이후 API 인증, 자동 로그인, 토큰 재발급에 사용됩니다.
             */
            tokenDataStore.saveTokens(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            )

            Result.success(
                LoginToken(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                ),
            )
        } catch (error: CancellationException) {
            // 코루틴 취소는 일반 로그인 실패로 처리하지 않고 그대로 전달
            throw error
        } catch (error: IllegalStateException) {
            // 서버 실패나 잘못된 응답으로 발생한 예외
            Result.failure(error)
        } catch (error: Exception) {
            // Retrofit, 네트워크, DataStore 등에서 발생한 예외
            Result.failure(
                IllegalStateException(
                    error.message ?: "로그인 처리 중 오류가 발생했습니다.",
                    error,
                ),
            )
        }
    }
}
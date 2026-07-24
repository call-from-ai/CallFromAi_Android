package kr.co.call.network.interceptor

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import kr.co.call.network.api.TokenReissueApi
import kr.co.call.network.dto.login.TokenReissueRequestDto
import okhttp3.Authenticator
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

/**
 * Access Token 만료로 401 응답이 발생했을 때 토큰을 재발급한다.
 * 재발급 성공 시 실패했던 요청의 토큰을 교체해 한 번 다시 요청한다.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val tokenReissueApi: TokenReissueApi,
) : Authenticator {

    /**
     * 여러 API 요청에서 동시에 401이 발생해도
     * 재발급 API가 중복 호출되지 않도록 재발급 영역을 보호한다.
     */
    private val refreshLock = Any()

    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        val requestPath =
            response.request.url.normalizedPath()

        /**
         * 로그인 API의 401은 로그인 실패를 의미하므로 재발급하지 않는다.
         * 재발급 API의 401도 다시 재발급하면 무한 반복되므로 종료한다.
         */
        if (requestPath in AUTH_FREE_PATHS) {
            return null
        }

        /**
         * 새 토큰으로 요청한 뒤에도 다시 401이 발생했다면
         * 더 이상 재시도하지 않고 요청을 종료한다.
         */
        if (responseCount(response) >= MAX_RESPONSE_COUNT) {
            return null
        }

        /**
         * 401이 발생한 요청에서 사용한 Access Token을 가져온다.
         * Authorization 헤더가 없었다면 재발급할 대상이 아니므로 종료한다.
         */
        val requestAccessToken = response.request
            .header("Authorization")
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
            ?: return null

        /**
         * Authenticator는 동기 방식으로 실행되므로 재발급이 끝날 때까지 기다린다.
         * 동기화 영역에서 한 요청만 재발급하고 나머지는 갱신된 토큰을 사용한다.
         */
        return synchronized(refreshLock) {
            runBlocking {
                val storedTokens =
                    tokenDataStore.getStoredTokens()

                val currentAccessToken =
                    storedTokens.accessToken

                val currentRefreshToken =
                    storedTokens.refreshToken

                /**
                 * 저장된 Access Token이나 Refresh Token이 없다면
                 * 재발급 요청을 만들 수 없으므로 종료한다.
                 */
                if (
                    currentAccessToken.isNullOrBlank() ||
                    currentRefreshToken.isNullOrBlank()
                ) {
                    return@runBlocking null
                }

                /**
                 * 잠금을 기다리는 동안 다른 요청이 이미 토큰을 재발급했다면
                 * 재발급 API를 다시 호출하지 않고 최신 토큰으로 재요청한다.
                 */
                if (requestAccessToken != currentAccessToken) {
                    return@runBlocking response.request
                        .withAccessToken(currentAccessToken)
                }

                /**
                 * 현재 저장된 Access Token과 Refresh Token을
                 * 서버 재발급 API에 전달한다.
                 */
                val reissueResponse = try {
                    tokenReissueApi.reissue(
                        request = TokenReissueRequestDto(
                            accessToken = currentAccessToken,
                            refreshToken = currentRefreshToken,
                        ),
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    Timber.e(
                        error,
                        "토큰 재발급 API 호출 실패",
                    )

                    return@runBlocking null
                }

                val reissueBody = reissueResponse.body()

                /**
                 * 응답의 result가 존재하고 두 토큰 모두 비어 있지 않을 때만
                 * 정상적인 재발급 결과로 사용한다.
                 */
                val newTokens = reissueBody
                    ?.result
                    ?.takeIf { tokens ->
                        tokens.accessToken.isNotBlank() &&
                                tokens.refreshToken.isNotBlank()
                    }

                /**
                 * HTTP 요청과 서버 응답이 모두 성공했을 때
                 * 새 토큰을 DataStore에 저장하고 기존 요청을 다시 실행한다.
                 */
                if (
                    reissueResponse.isSuccessful &&
                    reissueBody?.isSuccess == true &&
                    newTokens != null
                ) {
                    try {
                        tokenDataStore.saveTokens(
                            accessToken = newTokens.accessToken,
                            refreshToken = newTokens.refreshToken,
                        )
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Throwable) {
                        Timber.e(
                            error,
                            "재발급 토큰 저장 실패",
                        )

                        return@runBlocking null
                    }

                    return@runBlocking response.request
                        .withAccessToken(
                            newTokens.accessToken,
                        )
                }

                /**
                 * Refresh Token까지 만료되어 서버가 401을 반환한 경우
                 * 토큰을 모두 삭제해 잘못된 자동 로그인 상태를 해제한다.
                 */
                if (reissueResponse.code() == 401) {
                    try {
                        tokenDataStore.clearTokens()
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Throwable) {
                        Timber.e(
                            error,
                            "만료된 토큰 삭제 실패",
                        )
                    }
                }

                Timber.w(
                    "토큰 재발급 실패: httpCode=%s, code=%s, message=%s",
                    reissueResponse.code(),
                    reissueBody?.code,
                    reissueBody?.message,
                )

                null
            }
        }
    }

    /**
     * 실패했던 요청의 Authorization 헤더를
     * 새 Access Token이 포함된 Bearer 헤더로 교체한다.
     */
    private fun Request.withAccessToken(
        accessToken: String,
    ): Request {
        return newBuilder()
            .header(
                "Authorization",
                "Bearer $accessToken",
            )
            .build()
    }

    /**
     * 동일한 요청이 현재까지 몇 번 실행됐는지 확인한다.
     * 첫 401 이후 한 번만 재요청하도록 반복 횟수를 제한한다.
     */
    private fun responseCount(
        response: Response,
    ): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }

    /**
     * trailing slash를 제거한 정확한 전체 경로를 반환한다.
     * endsWith 비교로 다른 API가 잘못 제외되는 문제를 방지한다.
     */
    private fun HttpUrl.normalizedPath(): String {
        return encodedPath
            .trimEnd('/')
            .ifEmpty { "/" }
    }

    private companion object {
        const val MAX_RESPONSE_COUNT = 2

        /**
         * 현재 baseUrl이 /api/v1/까지 포함하므로
         * encodedPath의 전체 경로도 /api/v1/부터 작성한다.
         */
        val AUTH_FREE_PATHS = setOf(
            "/api/v1/auth/kakao",
            "/api/v1/auth/reissue",
        )
    }
}
package kr.co.call.network.interceptor

import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import kr.co.call.network.api.LoginApi
import kr.co.call.network.api.TokenReissueApi
import kr.co.call.network.dto.login.TokenReissueRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Access Token 만료로 401 응답이 발생했을 때 토큰 재발급을 처리한다.
 * 재발급 성공 시 기존 요청의 토큰을 교체한 뒤 실패했던 요청을 다시 실행한다.
 */
class TokenAuthenticator @Inject constructor (
    private val tokenDataStore: TokenDataStore,
    private val tokenReissueApi: TokenReissueApi,
): Authenticator {
    /**
     * 여러 API에서 동시에 401이 발생하더라도
     * 토큰 재발급 API가 중복 호출되지 않도록 동기화하는 객체
     */
    private val refreshLock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val requestPath = response.request.url.encodedPath

        /**
         * 로그인 요청의 401은 아이디 또는 토큰 문제이므로 재발급하지 않는다.
         * 재발급 요청의 401도 다시 재발급하면 무한 반복되므로 종료한다.
         */
        if (requestPath.endsWith("/auth/kakao") ||
            requestPath.endsWith("/auth/reissue")
            ) {
                return null
        }
        //새 토큰으로 재요청한 뒤에도 401이면 무한 재시도를 방지하고 종료
        if (responseCount(response) >= 2) {
            return null
        }

        //401이 발생한 요청에서 사용했던 Access Token을 가져옴
        val requestAccessToken = response.request
            .header("Authorization")
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
            ?: return null

        /**
         * 여러 요청이 동시에 401을 받았을 때 한 요청만 재발급을 진행한다.
         * 나머지 요청은 재발급이 끝날 때까지 기다린 후 최신 토큰을 사용한다.
         */
        synchronized(refreshLock) {
            return runBlocking {
                val currentAccessToken = tokenDataStore.getAccessToken()
                val currentRefreshToken = tokenDataStore.getRefreshToken()

                // 저장된 토큰이 없으면 재발급할 수 없으므로 종료
                if (currentAccessToken.isNullOrBlank() || currentRefreshToken.isNullOrBlank()) {
                    return@runBlocking null
                }

                /**
                 * 기다리는 동안 다른 요청이 이미 토큰 재발급을 완료했다면
                 * 재발급 API를 다시 호출하지 않고 최신 토큰으로 요청을 재시도한다.
                 */
                if (requestAccessToken != currentAccessToken) {
                    return@runBlocking response.request
                        .withAccessToken(currentAccessToken)
                }
                // 기존 Access Token과 Refresh Token으로 재발급 API 호출
                val reissueHttpResponse = runCatching {
                    tokenReissueApi.reissue(
                        request = TokenReissueRequestDto(
                            accessToken = currentAccessToken,
                            refreshToken = currentRefreshToken,
                        ),
                    )
                }.getOrNull()
                    ?: return@runBlocking null

                val reissueBody = reissueHttpResponse.body()
                val newTokens = reissueBody?.result

                /**
                 * 재발급에 성공하면 새 토큰을 DataStore에 저장하고
                 * 기존 요청의 Authorization 헤더를 새 Access Token으로 교체한다.
                 */
                if (reissueHttpResponse.isSuccessful &&
                    reissueBody?.isSuccess == true &&
                    newTokens != null
                    ) {
                    tokenDataStore.saveTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken,
                    )
                    return@runBlocking response.request
                        .withAccessToken(newTokens.accessToken)
                }
                /**
                 * Refresh Token까지 만료됐거나 서버에서 거절한 경우
                 * 자동 로그인을 방지하기 위해 저장된 토큰을 모두 삭제한다.
                 */
                if (reissueHttpResponse.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                null
            }
        }
    }

    /**
     * 401이 발생했던 기존 요청의 Authorization 헤더를
     * 새로 발급받은 Access Token으로 교체한다.
     */
    private fun Request.withAccessToken(
        accessToken: String,
    ): Request {
        return newBuilder()
            .header(
                "Authorization",
                "Bearer $accessToken"
            )
            .build()
    }

    /**
     * 동일한 요청이 몇 번 재시도됐는지 확인한다.
     * 반복 횟수를 제한해 401 응답에 의한 무한 요청을 방지한다.
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
}
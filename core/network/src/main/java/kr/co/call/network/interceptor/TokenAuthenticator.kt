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

class TokenAuthenticator @Inject constructor (
    private val tokenDataStore: TokenDataStore,
    private val tokenReissueApi: TokenReissueApi,
): Authenticator {
    private val refreshLock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val requestPath = response.request.url.encodedPath

        //로그인 요청에서 발생한 401은 토큰 재발급 대상이 아님
        //재발급 요청에서 다시 401이 발생하면 재발급을 반복하지 않음
        if (requestPath.endsWith("/auth/kakao") || requestPath.endsWith("/auth/reissue")) {
            return null
        }
        //새 토큰으로 재요청했는데 또 401 발생하면 무한 반복하지 않고 종료하게 함
        if (responseCount(response) >= 2) {
            return null
        }

        //401이 발생한 요청에서 사용했던 access토큰을 가져옴
        val requestAccessToken = response.request
            .header("Authorization")
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
            ?: return null

        //여러 api가 동시에 401을 받았을 때 한 번에 하나의 요청만 재발급을 수행함
        synchronized(refreshLock) {
            return runBlocking {
                val currentAccessToken = tokenDataStore.getAccessToken()
                val currentRefreshToken = tokenDataStore.getRefreshToken()

                //저장된 토큰이 없으면 재발급 할 수 없으므로 종료
                if (currentAccessToken.isNullOrBlank() || currentRefreshToken.isNullOrBlank()) {
                    return@runBlocking null
                }

                //기다리는 동안 다른 요청이 이미 재발급을 완료했다면 재발급 api를 다시 호출하지 않고 최신 토큰을 사용함
                if (requestAccessToken != currentAccessToken) {
                    return@runBlocking response.request
                        .withAccessToken(currentAccessToken)
                }
                //access token과 refresh token을 사용해 재발급api호출
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

                //재발급 성공
                if (reissueHttpResponse.isSuccessful && reissueBody?.isSuccess == true && newTokens != null) {
                    tokenDataStore.saveTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken,
                    )
                    return@runBlocking response.request
                        .withAccessToken(newTokens.accessToken)
                }
                //refresh token까지 만료되거나 거절된 경우 저장된 토큰 모두 삭제
                if (reissueHttpResponse.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                null
            }
        }
    }

    //기존 요청의 Authorization헤더를 새로운 access토큰으로 교체
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

    //같은 요청이 몇 번 반복됐는지 확인
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
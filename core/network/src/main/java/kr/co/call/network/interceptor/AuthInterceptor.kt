package kr.co.call.network.interceptor

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 인증이 필요한 API 요청의 Authorization 헤더에 Access Token을 추가한다.
 * 로그인과 토큰 재발급 요청은 인증 전 요청이므로 토큰을 추가하지 않는다.
 */
class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        //로그인 및 재발급 API에는 Authorization 헤더를 추가하지 않음
        if (originalRequest.url.encodedPath.isAuthFreePath()) {
            return chain.proceed(originalRequest)
        }
        //저장된 access 토큰이 없으면 원래 요청 그대로 전송
        val accessToken = tokenDataStore.getCachedAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        // 인증이 필요한 요청에 Bearer 형식의 Access Token을 추가
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(authenticatedRequest)
    }

    // 현재 요청 주소가 인증 토큰을 요구하지 않는 API인지 확인
    private fun String.isAuthFreePath(): Boolean{
    return AUTH_FREE_ENDPOINTS.any(::endsWith)
    }
    private companion object{
        val AUTH_FREE_ENDPOINTS=setOf(
            "/auth/kakao",
            "/auth/reissue",
        )
    }
}
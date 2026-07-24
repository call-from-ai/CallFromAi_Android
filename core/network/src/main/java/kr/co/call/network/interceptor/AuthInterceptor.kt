package kr.co.call.network.interceptor

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

//모든 요청에 access토큰을 헤더에 붙여주는 인터셉터
class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        //로그인 요청과 토큰 재발급 요청에는 헤더를 붙이지 않음
        if (originalRequest.url.encodedPath.isAuthFreePath()) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking {
            tokenDataStore.getAccessToken()
        }

        //저장된 access토큰이 없으면 기존 요청 그대로 전송
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(authenticatedRequest)
    }
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
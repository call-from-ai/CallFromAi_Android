package kr.co.call.network.interceptor

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken= runBlocking {
            tokenDataStore.accessToken.first()
        }

        val request=chain.request().newBuilder()
            .apply{
                if (!accessToken.isNullOrBlank()){
                    addHeader("Authorization", "Bearer $accessToken")
                }
            }
            .build()

        return chain.proceed(request)
    }
}
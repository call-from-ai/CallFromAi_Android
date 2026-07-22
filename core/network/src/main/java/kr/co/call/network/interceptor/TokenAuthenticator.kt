package kr.co.call.network.interceptor

import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kr.co.call.datastore.TokenDataStore
import kr.co.call.network.api.LoginApi
import kr.co.call.network.dto.login.TokenReissueRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor (
    private val tokenDataStore: TokenDataStore,
    private val loginApiProvider: Lazy<LoginApi>,
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val accessToken=runBlocking {tokenDataStore.accessToken.first()}
        val refreshToken=runBlocking { tokenDataStore.refreshToken.first()}
        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) return null

        return runBlocking {
            runCatching {
                loginApiProvider.get().reissue(
                    request= TokenReissueRequestDto(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                    ),
                )
            }.getOrNull()?.let{reissueResponse->
                val newTokens=reissueResponse.result
                if (reissueResponse.isSuccess && newTokens != null){
                    tokenDataStore.saveTokens(
                        accessToken=newTokens.accessToken,
                        refreshToken=newTokens.refreshToken,
                    )
                    response.request.newBuilder()
                        .header("Authorization","Bearer ${newTokens.accessToken}")
                        .build()
                }else{
                    tokenDataStore.clearTokens()
                    null
                }
            }
        }
    }
    //무한루프 방지용 (요청 몇 번이나 재시도했는지)
    private fun responseCount(response: Response):Int {
        var count=1
        var prior=response.priorResponse
        while (prior != null){
            count++
            prior=prior.priorResponse
        }
        return count
    }
}
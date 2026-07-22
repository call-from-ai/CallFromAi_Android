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
        if (responseCount(response) >= MAX_RETRY_COUNT) return null

        val accessToken=runBlocking {tokenDataStore.getAccessToken()}
        val refreshToken=runBlocking { tokenDataStore.getRefreshToken()}
        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()){ return null}
        return runBlocking {
            val reissueResponse=
            runCatching{
                loginApiProvider.get().reissue(
                    request= TokenReissueRequestDto(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                    ),
                )
            }.getOrNull()
                val newTokens=reissueResponse?.result
                if (reissueResponse?.isSuccess==true && newTokens != null){
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
    //무한루프 방지용 (요청 몇 번이나 재시도했는지)
    private fun responseCount(response: Response):Int {
        var count=1
        var priorResponse=response.priorResponse
        while (priorResponse != null){
            count++
            priorResponse=priorResponse.priorResponse
        }
        return count
    }
    private companion object {
        const val MAX_RETRY_COUNT=2
    }
}

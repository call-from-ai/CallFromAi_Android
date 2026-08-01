package kr.co.call.impl.auth

import kotlinx.coroutines.suspendCancellableCoroutine
import com.kakao.sdk.user.UserApiClient
import javax.inject.Inject
import kotlin.coroutines.resume

class KakaoLogoutManager @Inject constructor(){
    suspend fun logout(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.logout callback@{ error->
                if (!continuation.isActive){
                    return@callback
                }
                val result = if (error == null){
                    Result.success(Unit)
                }else {
                    Result.failure(error)
                }
                continuation.resume(result)
            }
        }
}
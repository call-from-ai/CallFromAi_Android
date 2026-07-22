package kr.co.call.impl.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import timber.log.Timber

class KakaoLoginManager {
    fun login(
        context: Context,
        onSuccess: (String)->Unit,
        onFailure: (Throwable)->Unit,
        onCancel:()->Unit,
    ){
        val kakaoAccountLoginCallback:
                (OAuthToken?, Throwable?)->Unit={token, error ->
                    when{
                        error != null ->{
                            Timber.e(error, "로그인 실패")
                            onFailure(error)
                        }
                        token != null ->{
                            Timber.d("로그인 성공")
                            onSuccess(token.accessToken)
                        }
                    }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)){
            UserApiClient.instance.loginWithKakaoTalk(context){token, error ->
                when {
                    error != null ->{
                        if(
                            error is ClientError && error.reason==ClientErrorCause.Cancelled
                        ){
                            Timber.d("카카오 로그인 취소")
                            onCancel()
                            return@loginWithKakaoTalk
                        }
                        Timber.e(error, "로그인 실패")
                        UserApiClient.instance.loginWithKakaoAccount(
                            context=context,
                            callback=kakaoAccountLoginCallback,
                        )
                    }
                    token != null ->{
                        Timber.d("로그인 성공")
                        onSuccess(token.accessToken)
                    }
                }
            }
        } else{
            UserApiClient.instance.loginWithKakaoAccount(
                context=context,
                callback=kakaoAccountLoginCallback,
            )
        }
    }
}
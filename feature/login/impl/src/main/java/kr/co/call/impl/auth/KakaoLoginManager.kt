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
        /*카카오계정 로그인 결과를 처리하는 콜백
        성공하면 카카오 Access Token을 ViewModel로 전달
         */
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

        //카카오톡 앱을 이용한 로그인이 가능한 경우 카카오톡 로그인을 먼저 시도
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
            //카카오톡 앱 로그인이 불가능하면 카카오계정 로그인으로 진행
        } else{
            UserApiClient.instance.loginWithKakaoAccount(
                context=context,
                callback=kakaoAccountLoginCallback,
            )
        }
    }
}
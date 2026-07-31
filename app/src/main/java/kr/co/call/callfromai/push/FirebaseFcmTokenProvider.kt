package kr.co.call.callfromai.push

import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import kr.co.call.domain.push.FcmTokenProvider
import timber.log.Timber

/**
 * Firebase Messaging 으로 기기 FCM 토큰을 발급한다.
 * 로그인 후 [kr.co.call.data.push.PushTokenManager] 가 이 토큰을 서버에 등록한다.
 */
@Singleton
class FirebaseFcmTokenProvider @Inject constructor() : FcmTokenProvider {

    override suspend fun getToken(): String? =
        try {
            FirebaseMessaging.getInstance().token.await()
                .takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            Timber.e(e, "FCM 토큰 발급 실패")
            null
        }
}

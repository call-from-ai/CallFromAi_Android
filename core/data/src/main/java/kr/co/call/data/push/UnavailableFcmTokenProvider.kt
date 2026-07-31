package kr.co.call.data.push

import javax.inject.Inject
import javax.inject.Singleton
import kr.co.call.domain.push.FcmTokenProvider
import timber.log.Timber

/**
 * Firebase Messaging 연동 전 placeholder
 * [getToken] 은 항상 null -> 서버 등록 훅은 no-op
 * google-services.json + Messaging SDK 붙이면 실제 Provider 로 교체한다.
 */
@Singleton
class UnavailableFcmTokenProvider @Inject constructor() : FcmTokenProvider {

    override suspend fun getToken(): String? {
        Timber.d("FCM 미연동: 토큰 발급 스킵")
        return null
    }
}

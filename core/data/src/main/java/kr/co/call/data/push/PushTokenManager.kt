package kr.co.call.data.push

import javax.inject.Inject
import javax.inject.Singleton
import kr.co.call.datastore.FcmTokenDataStore
import kr.co.call.domain.push.FcmTokenProvider
import kr.co.call.domain.repository.PushTokenRepository
import timber.log.Timber

/**
 * FCM 토큰 <-> 서버 /push-tokens 동기화
 *
 * - 로그인 직후: [registerCurrentDevice]
 * - 로그아웃/탈퇴: [unregisterCurrentDevice]
 *
 * 로그인/로그아웃 자체를 실패시키지 않도록 예외는 삼키고 로그만 남긴다.
 * Firebase 미연동 시 provider 가 null 이면 등록은 조용히 스킵한다.
 */
@Singleton
class PushTokenManager @Inject constructor(
    private val fcmTokenProvider: FcmTokenProvider,
    private val pushTokenRepository: PushTokenRepository,
    private val fcmTokenDataStore: FcmTokenDataStore,
) {

    /**
     * 기기 토큰을 서버에 등록하고 로컬에 저장한다.
     * JWT 가 이미 저장된 뒤(로그인 성공 후) 호출해야 한다.
     */
    suspend fun registerCurrentDevice() {
        val token = fcmTokenProvider.getToken()
        if (token.isNullOrBlank()) {
            Timber.d("FCM 토큰 없음 -> 서버 등록 스킵")
            return
        }
        pushTokenRepository.register(token)
            .onSuccess {
                fcmTokenDataStore.saveToken(token)
                Timber.d("FCM 토큰 서버 등록 성공")
            }
            .onFailure { error ->
                Timber.w(error, "FCM 토큰 서버 등록 실패")
            }
    }

    /**
     * 서버에서 토큰 해제 후 로컬 FCM 토큰을 지운다.
     * 서버 실패와 무관하게 로컬은 정리한다(로그아웃 완료 우선)
     */
    suspend fun unregisterCurrentDevice() {
        val token = fcmTokenDataStore.getToken()
            ?: fcmTokenProvider.getToken()

        if (!token.isNullOrBlank()) {
            pushTokenRepository.delete(token)
                .onSuccess { Timber.d("FCM 토큰 서버 삭제 성공") }
                .onFailure { error -> Timber.w(error, "FCM 토큰 서버 삭제 실패") }
        }

        fcmTokenDataStore.clearToken()
    }
}

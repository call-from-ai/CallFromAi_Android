package kr.co.call.domain.repository

/**
 * FCM 기기 토큰을 서버에 등록/해제한다.
 */
interface PushTokenRepository {

    /**
     * FCM 토큰 등록. platform 은 Android 앱 기준 ANDROID 로 전송한다.
     */
    suspend fun register(token: String): Result<Unit>

    /**
     * 로그아웃 등에서 해당 기기 토큰 해제
     */
    suspend fun delete(token: String): Result<Unit>
}

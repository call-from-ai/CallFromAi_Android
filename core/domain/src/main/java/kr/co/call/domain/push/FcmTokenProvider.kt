package kr.co.call.domain.push

/**
 * 기기 FCM 등록 토큰 제공
 * Firebase SDK 연동 전 구현체는 null을 반환해도 된다.
 */
interface FcmTokenProvider {

    /**
     * 현재 기기의 FCM 토큰
     * 미연동/실패 시 null (호출 측은 등록을 건너뛴다)
     */
    suspend fun getToken(): String?
}

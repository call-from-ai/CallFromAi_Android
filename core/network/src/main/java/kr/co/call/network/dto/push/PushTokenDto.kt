package kr.co.call.network.dto.push

/**
 * FCM 기기 토큰 등록 요청
 */
data class PushTokenRegisterRequestDto(
    val token: String,
    val platform: String = PLATFORM_ANDROID,
)

/**
 * FCM 기기 토큰 삭제(로그아웃) 요청
 */
data class PushTokenDeleteRequestDto(
    val token: String,
)

/** 이 앱은 Android 전용이므로 등록 시 기본값으로 사용한다 */
const val PLATFORM_ANDROID = "ANDROID"

package kr.co.call.api

/**
 * 벨소리 최대 지속 시간(ms).
 * 이 시간 안에 수락/거절이 없으면 알림과 착신 화면이 자동으로 정리됩니다.
 *
 * 알림(app 모듈)과 착신 화면(feature:call:impl) 양쪽에서 같은 값을 참조해야 해서
 * 두 모듈이 공통으로 의존하는 이 모듈에 둡니다.
 */
const val RING_TIMEOUT_MILLIS = 60_000L

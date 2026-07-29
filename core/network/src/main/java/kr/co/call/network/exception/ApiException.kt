package kr.co.call.network.exception

/**
 * Retrofit / HTTP 계층 전용 예외
 *
 * - network 모듈 안에서만 생성한다.
 * - data 레이어가 [kr.co.call.domain.exception.AppException] 으로 변환한다.
 * - feature / domain 은 이 타입을 직접 보지 않는 것이 목표다.
 *
 * @property httpCode HTTP status (예: 401, 500). body-only 실패면 null 가능
 * @property serverCode BE `code` (예: AUTH401_3, CALL409_1)
 */
class ApiException(
    val httpCode: Int?,
    val serverCode: String?,
    override val message: String?,
    cause: Throwable? = null,
) : Exception(message, cause)

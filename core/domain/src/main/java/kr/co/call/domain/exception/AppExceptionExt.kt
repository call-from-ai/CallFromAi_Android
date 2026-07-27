package kr.co.call.domain.exception

import kr.co.call.domain.util.LoadStatus

/**
 * ViewModel / UI 에서 [AppException]·[Throwable] 을 다룰 때 쓰는 공통 헬퍼
 *
 * Repository 실패는 보통 [AppException] 이지만, 도메인 특수 예외 등이
 * 섞일 수 있으므로 수신 타입은 [Throwable] 기준으로 둔다.
 */

/** 화면에 보여줄 사용자용 메시지 */
fun Throwable.toUserMessage(
    default: String = "오류가 발생했습니다.",
): String =
    when (this) {
        is AppException -> message?.takeIf { it.isNotBlank() } ?: default
        else -> message?.takeIf { it.isNotBlank() } ?: default
    }

/** 인증 만료/유효하지 않은 토큰 등 -> 로그인 플로우 */
fun Throwable.isUnauthorized(): Boolean =
    this is AppException.Unauthorized

/** 권한 없음 */
fun Throwable.isForbidden(): Boolean =
    this is AppException.Forbidden

/** 오프라인·타임아웃 등 클라 네트워크 실패 */
fun Throwable.isNetworkError(): Boolean =
    this is AppException.Network

/** 서버/외부 연동 5xx 계열 */
fun Throwable.isServerError(): Boolean =
    this is AppException.Server

/**
 * 재시도 UI를 보여줄 만한 실패인지
 * [AppException.Network], [AppException.Server] 를 true 로 본다.
 */
fun Throwable.isRetryable(): Boolean =
    this is AppException.Network || this is AppException.Server

/** [AppException] 이면 그대로, 아니면 null */
fun Throwable.asAppException(): AppException? =
    this as? AppException

/**
 * 로딩 실패 [LoadStatus.Error] 로 변환
 * message + optional cause 를 한 번에 맞출 때 사용
 */
fun Throwable.toLoadStatusError(
    defaultMessage: String = "오류가 발생했습니다.",
): LoadStatus.Error =
    LoadStatus.Error(
        message = toUserMessage(defaultMessage),
        cause = asAppException(),
    )

package kr.co.call.data.util

import java.io.IOException
import java.net.SocketTimeoutException
import kr.co.call.domain.exception.AppException
import kr.co.call.network.exception.ApiException
import kotlin.coroutines.cancellation.CancellationException

/**
 * network / 기타 [Throwable] -> domain [AppException] 변환
 *
 * Repository 는 [toFailure] 로 실패를 정규화한다.
 * feature / ViewModel 은 [AppException] 만 보면 된다.
 */
fun Throwable.toAppException(): AppException {
    if (this is AppException) return this

    return when (this) {
        is ApiException -> mapApiException(this)
        is SocketTimeoutException ->
            AppException.Network(message = "응답 시간이 초과되었습니다.", cause = this)
        is IOException ->
            AppException.Network(cause = this)
        else ->
            AppException.Unknown(
                message = message ?: "알 수 없는 오류가 발생했습니다.",
                cause = this,
            )
    }
}

/**
 * [Result.failure] 에 넣을 예외로 정규화한다.
 *
 * - [CancellationException]: 코루틴 취소이므로 [Result] 에 넣지 않고 다시 던진다
 * - 이미 [AppException]: 유지
 * - 그 외: [toAppException]
 *
 * 보통 [toAppResult] / [safeApiResult] 경로에서 호출한다.
 */
fun Throwable.toFailure(): Throwable =
    when (this) {
        is CancellationException -> throw this
        is Error -> throw this
        is AppException -> this
        else -> toAppException()
    }

/**
 * stdlib [runCatching] 결과의 실패를 [toFailure] 로 정규화한다.
 *
 * - 성공: 그대로
 * - 실패: [AppException] 등으로 변환. 취소·Error 는 [toFailure] 가 rethrow
 *
 * ```
 * runCatching { ... }.toAppResult()
 * ```
 */
fun <T> Result<T>.toAppResult(): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it.toFailure()) },
    )

/**
 * BE `code` / HTTP status -> [AppException] 카테고리
 *
 * 코드 예: AUTH401_3, CHARACTER403_1, CALL409_2, COMMON500_1, AI502_1
 * 개별 code 를 enum 으로 복제하지 않고 prefix·패턴으로 묶는다.
 */
private fun mapApiException(e: ApiException): AppException {
    val code = e.serverCode.orEmpty()
    val msg = e.message ?: "요청을 처리하지 못했습니다."
    val http = e.httpCode

    return when {
        // AUTH401_* 또는 HTTP 401
        code.contains("401_") || http == 401 ->
            AppException.Unauthorized(
                code = code.ifEmpty { "AUTH401" },
                message = msg,
                cause = e,
            )

        // AUTH403_*, CHARACTER403_*, CHAT403_*, CALL403_* 또는 HTTP 403
        code.contains("403_") || http == 403 ->
            AppException.Forbidden(
                code = code.ifEmpty { "FORBIDDEN" },
                message = msg,
                cause = e,
            )

        // *404_* 또는 HTTP 404
        code.contains("404_") || http == 404 ->
            AppException.NotFound(
                code = code.ifEmpty { "NOT_FOUND" },
                message = msg,
                cause = e,
            )

        // *409_* 또는 HTTP 409
        code.contains("409_") || http == 409 ->
            AppException.Conflict(
                code = code.ifEmpty { "CONFLICT" },
                message = msg,
                cause = e,
            )

        // *500_*, *502_*, *503_* 또는 HTTP 5xx
        code.contains("500_") ||
            code.contains("502_") ||
            code.contains("503_") ||
            (http != null && http >= 500) ->
            AppException.Server(
                code = code.ifEmpty { null },
                message = msg,
                cause = e,
            )

        // COMMON400_*, TERM400_*, CHARACTER400_*, CALL400_* 등 나머지 비즈니스
        else ->
            AppException.Business(
                code = code.ifEmpty { "UNKNOWN" },
                message = msg,
                cause = e,
            )
    }
}

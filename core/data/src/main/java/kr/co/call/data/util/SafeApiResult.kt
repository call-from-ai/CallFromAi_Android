package kr.co.call.data.util

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall
import kr.co.call.network.util.safeApiCallUnit

//예외처리파일
/**
 * Repository 작업에서 발생한 예외를 앱의 도메인 예외로 변환한다.
 * 코루틴 취소와 치명적인 [Error]는 [toAppResult]에서 다시 전파된다.
 */
suspend fun <T> runRepositoryCatching(
    block: suspend () -> T,
): Result<T> =
    runCatching {
        block()
    }.toAppResult()

/**
 * [safeApiCall] + stdlib [runCatching] + [toAppResult] 조합.
 * Repository 에서 runCatching / fold 보일러플레이트 없이 바로 [Result] 를 받는다.
 *
 * ```
 * safeApiResult(parser) { api.getFoo() }.map { it.toDomain() }
 * ```
 */
suspend fun <T : Any> safeApiResult(
    parser: ErrorResponseParser,
    call: suspend () -> ApiResponse<T>,
): Result<T> =
    runCatching {
        safeApiCall(parser, call)
    }.toAppResult()

/**
 * [safeApiCallUnit] + stdlib [runCatching] + [toAppResult] 조합 (result 없는 성공 API).
 */
suspend fun safeApiResultUnit(
    parser: ErrorResponseParser,
    call: suspend () -> ApiResponse<*>,
): Result<Unit> =
    runCatching {
        safeApiCallUnit(parser, call)
    }.toAppResult()

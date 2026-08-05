package kr.co.call.data.util

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall
import kr.co.call.network.util.safeApiCallUnit

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

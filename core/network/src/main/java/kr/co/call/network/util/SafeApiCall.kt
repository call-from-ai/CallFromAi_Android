package kr.co.call.network.util

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.exception.ApiException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Retrofit suspend 호출을 감싸 성공 시 result 만 반환하고,
 * 실패 시 [ApiException] 으로 통일한다.
 *
 * 처리 케이스:
 * 1) HTTP 2xx + isSuccess=true + result 존재 -> result 반환
 * 2) HTTP 2xx + isSuccess=false -> ApiException (serverCode/message)
 * 3) HTTP 2xx + isSuccess=true + result null -> ApiException (빈 결과)
 * 4) HTTP 4xx/5xx (HttpException) -> body 파싱 후 ApiException
 * 5) 이미 ApiException -> 그대로
 * 6) CancellationException -> 그대로 rethrow
 *
 * result 가 없는 성공(BE Void) 은 [safeApiCallUnit] 사용
 */
suspend fun <T : Any> safeApiCall(
    parser: ErrorResponseParser,
    call: suspend () -> ApiResponse<T>,
): T {
    return try {
        val response = call()
        response.throwIfNotSuccess()
        response.result
            ?: throw ApiException(
                httpCode = null,
                serverCode = response.code,
                message = response.message.ifBlank { "응답 결과가 없습니다." },
            )
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        throw e
    } catch (e: HttpException) {
        throw e.toApiException(parser)
    }
}

/**
 * 정상 응답에서 result가 없을 수 있는 조회 API를 처리합니다.
 */
suspend fun <T : Any> safeApiCallNullable(
    parser: ErrorResponseParser,
    call: suspend () -> ApiResponse<T>,
): T? {
    return try {
        val response = call()
        response.throwIfNotSuccess()
        response.result
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        throw e
    } catch (e: HttpException) {
        throw e.toApiException(parser)
    }
}

/**
 * 성공 시 result 가 null 이어도 되는 API (BE `ApiResponse<Void>` / result 없음).
 */
suspend fun safeApiCallUnit(
    parser: ErrorResponseParser,
    call: suspend () -> ApiResponse<*>,
) {
    try {
        val response = call()
        response.throwIfNotSuccess()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        throw e
    } catch (e: HttpException) {
        throw e.toApiException(parser)
    }
}

private suspend fun HttpException.toApiException(parser: ErrorResponseParser): ApiException {
    val body = parser.parse(this)
    return ApiException(
        httpCode = code(),
        serverCode = body?.code,
        message = body?.message ?: message(),
        cause = this,
    )
}


private fun ApiResponse<*>.throwIfNotSuccess() {
    if (!isSuccess) {
        throw ApiException(
            httpCode = null,
            serverCode = code,
            message = message.ifBlank { "요청을 처리하지 못했습니다." },
        )
    }
}


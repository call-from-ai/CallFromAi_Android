package kr.co.call.data.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository 계층의 예외를 공통 실패 타입으로 변환합니다.
 */
suspend fun <T> runRepositoryCatching(
    block: suspend () -> T,
): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e.toFailure())
    }

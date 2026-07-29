package kr.co.call.data.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository 계층 공통 Result 래퍼
 *
 * - 성공: [Result.success]
 * - 코루틴 취소: [CancellationException] 을 그대로 던져 Orbit/VM 이 취소로 처리
 * - 그 외 실패: [toFailure] 로 정규화한 뒤 [Result.failure]
 *
 * 사용 예:
 * ```
 * override suspend fun getSummary(): Result<HomeSummary> =
 *     runRepositoryCatching {
 *         safeApiCall(errorResponseParser) { homeApi.getSummary() }.toDomain()
 *     }
 * ```
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

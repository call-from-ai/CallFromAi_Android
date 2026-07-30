package kr.co.call.network.dto

/**
 * BE 공통 API envelope
 *
 * ```
 * { "isSuccess": true/false, "code": "COMMON200", "message": "...", "result": ... }
 * ```
 *
 * Retrofit 성공 응답(2xx) 바디 매핑에 사용한다.
 * HTTP 에러(4xx/5xx) 바디는 [ErrorBodyDto] 로 파싱한다.
 */
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T? = null,
)

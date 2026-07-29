package kr.co.call.network.dto

/**
 * HTTP 에러 응답 body 파싱용 최소 DTO
 *
 * generic [ApiResponse] 를 Gson 으로 직접 읽으면 type erasure 로 깨질 수 있어
 * code / message 만 안전하게 꺼낸다.
 *
 * 예:
 * ```
 * { "isSuccess": false, "code": "COMMON500_1", "message": "서버 내부 오류가 발생했습니다.", "result": null }
 * ```
 */
data class ErrorBodyDto(
    val isSuccess: Boolean? = null,
    val code: String? = null,
    val message: String? = null,
)

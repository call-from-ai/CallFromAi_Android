package kr.co.call.domain.exception

/**
 * 앱 전역에서 쓰는 공통 예외
 *
 * - UI / ViewModel / UseCase 는 이 타입만 보고 분기한다.
 * - 서버 `code`(예: AUTH401_3, CALL409_1) 문자열을 enum 으로 전부 복제하지 않는다.
 *   data 매퍼가 code/HTTP 를 아래 카테고리로 묶는다.
 * - 네트워크 레이어 전용 예외(ApiException 등)는 여기에 두지 않는다.
 *
 * BE 응답 형태:
 * ```
 * { "isSuccess": false, "code": "COMMON500_1", "message": "...", "result": null }
 * ```
 */
sealed class AppException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause) {

    /**
     * 클라이언트 측 네트워크 실패
     * 서버 code 가 아니라 IOException / timeout / UnknownHost 등
     */
    class Network(
        message: String = "네트워크 연결을 확인해 주세요.",
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 인증 실패 -> 재로그인 / 토큰 갱신 플로우
     * BE: AUTH401_1 ~ AUTH401_4
     */
    class Unauthorized(
        val code: String,
        message: String = "로그인이 필요합니다.",
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 인증은 됐지만 권한 없음
     * BE: AUTH403_*, CHARACTER403_*, CHAT403_*, CALL403_*
     */
    class Forbidden(
        val code: String,
        message: String = "접근 권한이 없습니다.",
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 리소스/엔드포인트 없음
     * BE: COMMON404_*, MEMBER404_*, CHARACTER404_*, CHAT404_*,
     *     RELATIONSHIP404_*, CALL404_*
     */
    class NotFound(
        val code: String,
        message: String = "요청한 정보를 찾을 수 없습니다.",
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 상태 충돌 / 지금은 불가 (재시도/조건 안내)
     * BE: COMMON409_*, AI409_*, CALL409_*
     */
    class Conflict(
        val code: String,
        message: String,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 요청/비즈니스 규칙 위반. 서버 message 를 그대로 보여주는 경우가 많음
     * BE: COMMON400_*, TERM400_*, CHARACTER400_*, CHAT400_*, CALL400_*, EXTERNAL400_*
     */
    class Business(
        val code: String,
        message: String,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 서버/외부 연동 장애 -> 재시도 유도
     * BE: COMMON500_*, EXTERNAL500_*, AI502_*, AI503_*
     */
    class Server(
        val code: String? = null,
        message: String = "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
        cause: Throwable? = null,
    ) : AppException(message, cause)

    /**
     * 파싱 실패, 매핑 실패, 분류 불가
     */
    class Unknown(
        message: String = "알 수 없는 오류가 발생했습니다.",
        cause: Throwable? = null,
    ) : AppException(message, cause)
}

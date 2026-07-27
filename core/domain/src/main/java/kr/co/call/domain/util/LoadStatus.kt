package kr.co.call.domain.util

import kr.co.call.domain.exception.AppException

/**
 * 데이터 로딩 작업의 현재 상태를 나타냅니다.
 *
 * 비동기 작업의 UI 상태(로딩 / 성공·유휴 / 실패)를 타입 안전하게 모델링합니다.
 *
 * [Error.cause] 는 optional 입니다.
 * - Repository 실패가 [AppException] 이면 VM 에서 message 와 함께 넣을 수 있습니다.
 * - 기존처럼 message 만 넣어도 됩니다 (`cause` 기본값 null)
 */
sealed interface LoadStatus {
    data object Idle : LoadStatus
    data object Loading : LoadStatus

    /**
     * @param message 화면에 보여줄 문구
     * @param cause 실패 종류. 로그인 이동·재시도 등 분기용. 없으면 null
     */
    data class Error(
        val message: String,
        val cause: AppException? = null,
    ) : LoadStatus
}

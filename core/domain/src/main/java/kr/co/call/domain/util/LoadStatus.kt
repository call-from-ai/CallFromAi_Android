package kr.co.call.domain.util

/**
 * 데이터 로딩 작업의 현재 상태를 나타냅니다.
 *
 * 이 sealed 인터페이스는 주로 비동기 작업의 UI 상태를 모델링하는 데 사용되며,
 * 로딩 중, 성공, 실패와 같은 다양한 단계를 타입 안전(type-safe)하게 처리할 수 있도록 합니다.
 */
sealed interface LoadStatus {
    data object Idle : LoadStatus
    data object Loading : LoadStatus
    data class Error(val message: String) : LoadStatus
}
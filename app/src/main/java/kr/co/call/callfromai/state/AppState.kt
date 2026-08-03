package kr.co.call.callfromai.state

/**
 * 앱 전역에서 관리되어야 하는 상태를 정의합니다.
 *
 * 특정 화면에 종속되지 않고 앱의 생명주기 동안 유지되어야 하는 상태를 여기에 추가합니다.
 * 예를 들면 이런 식(예시일뿐임)
 *
 * ```
 *  *
 *  * data class AppState(
 *  *     val authState: AppAuthState = AppAuthStae.Loading,
 * )
 * ```
 */
data class AppState(
    val authState: AppAuthState = AppAuthState.Loading,
)
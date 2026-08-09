package kr.co.call.callfromai.intent

/**
 * 앱 전역에서 발생하는 사용자 액션 또는 시스템 이벤트를 정의합니다.
 *
 * 특정 화면에 종속되지 않고 앱 레벨에서 처리되어야 하는 인텐트를 여기에 추가합니다.
 * 예를 들면 이런 식(예시일뿐임)
 *
 * ```
 * sealed interface AppIntent {
 *     data class OnNotificationIntent(val intent: Intent) : AppIntent
 * }
 *
 * // MainActivity
 * override fun onNewIntent(intent: Intent) {
 *     super.onNewIntent(intent)
 *     viewModel.handleIntent(AppIntent.OnNotificationIntent(intent))
 * }
 * ```
 */
sealed interface AppIntent {
    data class LoginSucceeded(
        val needsOnboarding: Boolean,
    ): AppIntent

    data object LogoutSucceeded: AppIntent

    data class OnChatPushTapped(
        val chatRoomId: Long,
    ) : AppIntent

    data object OnNoticePushTapped : AppIntent
}
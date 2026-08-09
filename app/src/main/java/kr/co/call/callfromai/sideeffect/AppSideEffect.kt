package kr.co.call.callfromai.sideeffect

/**
 * 앱 전역에서 발생하는 일회성 이벤트를 정의합니다.
 *
 * 특정 화면에 종속되지 않고 앱 레벨에서 처리되어야 하는 사이드이펙트를 여기에 추가합니다.
 * 예를 들면 이런 식(예시일뿐임)
 *
 * ```
 * // MainActivity — 알림 인텐트 수신 후 ViewModel로 전달
 * override fun onNewIntent(intent: Intent) {
 *     super.onNewIntent(intent)
 *     viewModel.handleIntent(AppIntent.OnNotificationIntent(intent))
 * }
 *
 * // AppViewModel — SideEffect로 변환
 * is AppIntent.OnNotificationIntent -> intent {
 *     postSideEffect(AppSideEffect.NavigateToChatRoom(chatRoomId))
 * }
 *
 * // AppScreen — SideEffect 수집 후 네비게이션 처리
 * viewModel.container.sideEffectFlow.collect { effect ->
 *     when (effect) {
 *         is AppSideEffect.NavigateToChatRoom -> navController.navigate(...)
 *     }
 * }
 * ```
 */
sealed interface AppSideEffect {
    data object NavigateToLogin : AppSideEffect
    data class NavigateToChatRoom(val chatRoomId: Long) : AppSideEffect
    data object NavigateToHome : AppSideEffect
}

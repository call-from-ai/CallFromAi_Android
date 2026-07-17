package kr.co.call.callfromai

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.callfromai.ui.MainTab
import kr.co.call.callfromai.util.toNavKey

/**
 * 앱 전체 화면 전환을 담당하는 내비게이터입니다.
 *
 * Login → Onboarding → 탭 화면(Home / Chatting / MyPage) 간 이동과
 * 탭 전환을 단일 백스택 위에서 관리합니다.
 *
 * [AppScreen]에서 생성되며, 각 Feature의 Entry에 필요한 내비게이션 함수를
 * 전달하여 화면 이동을 처리합니다.
 *
 * ## 사용 예시
 *
 * ```
 * entryProvider = entryProvider {
 *
 *     loginEntry(
 *         navigateToOnboarding = {
 *             appNavigator.replaceAll(OnboardingNavKey)
 *         }
 *     )
 *
 *     onboardingEntry(
 *         navigateToHome = {
 *             appNavigator.replaceAll(HomeNavKey)
 *         }
 *     )
 * }
 * ```
 *
 * @param backStack [AppScreen]에서 생성된 내비게이션 백스택
 */
class AppNavigator(
    private val backStack: NavBackStack<NavKey>
) {

    // ======== 유틸 함수. 만약 다른 동작이 필요하다면 메서드 추가하셔두 될 듯? =========

    /**
     * 지정된 메인 탭으로 이동합니다.
     *
     * 기존 백스택에 존재하는 탭 관련 화면들을 모두 제거하고,
     * 홈 화면을 기반으로 선택한 탭 화면을 백스택에 추가하여 탭 전환을 수행합니다.
     * 현재 이미 해당 탭의 루트 화면에 있다면 아무 작업도 수행하지 않습니다.
     *
     * @param tab 이동하려는 대상 [MainTab]
     */
    fun navigateToTab(tab: MainTab) {

        // 선택한 탭에 대응하는 루트 NavKey를 가져옴.
        val targetKey = when (tab) {
            MainTab.HOME -> HomeNavKey
            MainTab.CHATTING -> ChattingNavKey
            MainTab.MYPAGE -> MyPageNavKey
        }

        // 이미 해당 탭의 루트 화면이면 아무 작업도 하지 않음
        if (backStack.lastOrNull() == targetKey) return

        // 첫 번째 탭 화면(Home/Chatting/MyPage)의 위치를 찾음
        val firstTabIndex = backStack.indexOfFirst {
            it is HomeNavKey || it is ChattingNavKey || it is MyPageNavKey
        }

        if (firstTabIndex >= 0) {
            while (backStack.size > firstTabIndex) {
                // 현재 탭 영역의 백스택을 모두 제거하여 선택한 탭의 루트 화면으로 이동
                backStack.removeAt(backStack.lastIndex)
            }
        }

        // Home를 탭 영역의 루트로 추가
        backStack += HomeNavKey

        // Home 탭이 아닌 경우 선택한 탭을 Home 위에 추가
        if (targetKey != HomeNavKey) {
            backStack += targetKey
        }
    }

    fun navigate(key: NavKey) {
        backStack += key
    }

    fun popBackStack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun replaceAll(key: NavKey) {
        backStack.clear()
        backStack += key
    }

    // TODO: 모듈 간의 내비게이션 이동이 필요할 때 메서드를 작성
    // 예를 들면 온보딩 화면 -> 홈 화면, 로그인 화면 -> 온보딩 화면 등
}

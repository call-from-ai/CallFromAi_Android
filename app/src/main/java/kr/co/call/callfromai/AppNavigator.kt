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
 * @param backStack [AppScreen]에서 생성된 내비게이션 백스택
 */
class AppNavigator(
    private val backStack: NavBackStack<NavKey>
) {

    // ======== 유틸 함수. 만약 다른 동작이 필요하다면 메서드 추가하셔두 될 듯? =========

    /**
     * 탭을 전환합니다.
     *
     * 백스택에서 첫 번째 탭 키 위치부터 전부 제거한 뒤,
     * HomeNavKey를 루트로 두고 선택된 탭 키를 추가합니다.
     */
    fun navigateToTab(tab: MainTab) {
        val firstTabIndex = backStack.indexOfFirst {
            it is HomeNavKey || it is ChattingNavKey || it is MyPageNavKey
        }
        if (firstTabIndex >= 0) {
            while (backStack.size > firstTabIndex) backStack.removeAt(backStack.lastIndex)
        }
        backStack.add(HomeNavKey)
        if (tab != MainTab.HOME) backStack.add(tab.toNavKey())
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

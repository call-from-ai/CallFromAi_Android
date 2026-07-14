package kr.co.call.impl

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.impl.component.MainTab
import kr.co.call.impl.util.toNavKey

/**
 * 각 피쳐 모듈 간 화면 전환을 담당하는 내비게이터입니다.
 *
 * [MainScreen]에서 생성된 후, TODO에 명시된 것처럼 모듈 간 이동 함수를 추가하고
 * 해당 함수를 각 Entry 컴포저블의 파라미터로 주입하는 방식으로 사용합니다.
 *
 * ## [MainScreen]에서의 사용 예시
 *
 * ```kotlin
 * entryProvider = entryProvider {
 *     homeEntry(
 *         navigateToCall = mainNavigator::navigateToCall
 *     )
 *     chattingEntry(
 *         navigateToCall = mainNavigator::navigateToCall
 *     )
 * }
 * ```
 *
 *
 * @param backStack [MainScreen]에서 생성된 내비게이션 백스택
 */
class MainNavigator(
    private val backStack: NavBackStack<NavKey>
) {
    // ======== 유틸 함수. 만약 다른 동작이 필요하다면 메서드 추가하셔두 될 듯? =========

    // 바텀 탭 클릭을 통한 이동
    fun navigateToTab(tab: MainTab) {
        backStack.clear()
        backStack.add(HomeNavKey)

        if (tab != MainTab.HOME) {
            backStack.add(tab.toNavKey())
        }
    }

    // 백스택에 화면 추가
    fun navigate(key: NavKey) {
        backStack += key
    }

    // 현재 화면 제거(백스택 최상단에서 걷어냄)
    fun popBackStack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    //  백스택 초기화 후 새 화면으로 이동
    fun replaceAll(key: NavKey) {
        backStack.clear()
        backStack += key
    }

    // TODO: 모듈 간의 내비게이션 이동이 필요할 때 메서드를 작성
    // 예를 들면 채팅 화면 -> 통화 화면 이라던가.. 홈 화면 -> 통화 화면 이라던가..
}
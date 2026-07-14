package kr.co.call.callfromai

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Login - Onboarding - Main 모듈 간 화면 전환을 담당하는 내비게이터입니다.
 *
 * [AppScreen]에서 생성된 후, TODO에 명시된 것처럼 모듈 간 이동 함수를 추가하고
 * 해당 함수를 각 Entry 컴포저블의 파라미터로 주입하는 방식으로 사용합니다.
 *
 * ## AppScreen에서의 사용 예시
 *
 * entryProvider = entryProvider {
 *     loginEntry(
 *         navigateToOnboarding = appNavigator::navigateToOnboarding
 *     )
 *     onboardingEntry(
 *         navigateToMain = appNavigator::navigateToMain
 *     )
 * }
 *
 *
 * @param backStack [AppScreen]에서 생성된 내비게이션 백스택
 */
class AppNavigator(
    private val backStack: NavBackStack<NavKey>
) {

    // ======== 유틸 함수. 만약 다른 동작이 필요하다면 메서드 추가하셔두 될 듯? =========

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
    // 예를 들면 온보딩 화면 -> 통화 화면 이라던가.. 로그인 화면 -> 온보딩 화면 이라던가..

}
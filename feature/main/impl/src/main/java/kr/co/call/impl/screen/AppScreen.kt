package kr.co.call.impl.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kr.co.call.api.LoginNavKey
import kr.co.call.api.MainNavKey
import kr.co.call.impl.entry.loginEntry
import kr.co.call.impl.entry.mainEntry
import kr.co.call.impl.entry.onboardingEntry

/**
 * 애플리케이션 화면 내비게이션의 메인 진입점입니다.
 *
 * 이 컴포저블은 내비게이션 백스택을 초기화하고 관리하며, 로그인, 온보딩, 메인 화면과 같은
 * 서로 다른 기능 모듈 간의 화면 전환을 처리합니다.
 *
 * @param modifier 루트 내비게이션 디스플레이에 적용할 [Modifier]
 */
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: 로그인 화면 구현 후 로그인 여부에 따른 분기처리 필요
    val backStack = rememberNavBackStack(MainNavKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            loginEntry()
            onboardingEntry()
            mainEntry()
        }
    )
}

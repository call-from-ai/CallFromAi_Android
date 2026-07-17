package kr.co.call.callfromai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.callfromai.ui.MainBottomBar
import kr.co.call.callfromai.ui.MainTab
import kr.co.call.impl.entry.chattingEntry
import kr.co.call.impl.entry.homeEntry
import kr.co.call.impl.entry.loginEntry
import kr.co.call.impl.entry.myPageEntry
import kr.co.call.impl.entry.onboardingEntry
import kr.co.call.callfromai.util.toMainTab

/**
 * 애플리케이션 화면 내비게이션의 메인 진입점입니다.
 *
 * 단일 백스택으로 로그인/온보딩/탭 화면을 모두 관리하며,
 * 현재 백스택 최상단 키를 기준으로 BottomBar 표시 여부를 결정합니다.
 *
 * @param modifier 루트 [Box]에 적용할 [Modifier]
 */
@Composable
fun AppScreen(modifier: Modifier = Modifier) {
    // TODO: 로그인 구현 후 로그인 여부에 따른 분기처리 필요. 일단은 시작점을 홈 화면으로 설정
    val backStack = rememberNavBackStack(HomeNavKey)

    val appNavigator = remember(backStack) { AppNavigator(backStack) }
    val currentKey = backStack.lastOrNull()

    // TODO: 각자 구현하면서 피그마 보고 추가하기!!
    val showBottomBar = when (currentKey) {
        is HomeNavKey,
        is ChattingNavKey,
        is MyPageNavKey -> true

        else -> false
    }

    val currentTab = currentKey?.toMainTab() ?: MainTab.HOME

    Box(modifier = modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .fillMaxSize(),
            entryProvider = entryProvider {
                loginEntry()
                onboardingEntry()
                homeEntry()
                chattingEntry(
                    navigateToChatRoom = {
                        appNavigator.navigate(ChatRoomNavKey(it))
                    },
                    onBack = {
                        appNavigator.popBackStack()
                    }
                )

                myPageEntry()
            }
        )
        if (showBottomBar) {
            MainBottomBar(
                currentTab = currentTab,
                onTabSelected = appNavigator::navigateToTab,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

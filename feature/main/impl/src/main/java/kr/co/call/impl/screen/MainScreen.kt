package kr.co.call.impl.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kr.co.call.api.HomeNavKey
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.impl.component.MainBottomBar
import kr.co.call.impl.component.MainTab
import kr.co.call.impl.entry.chattingEntry
import kr.co.call.impl.entry.homeEntry
import kr.co.call.impl.entry.myPageEntry
import kr.co.call.impl.util.toMainTab
import kr.co.call.impl.util.toNavKey
import kr.co.call.impl.viewmodel.MainIntent
import kr.co.call.impl.viewmodel.MainSideEffect
import kr.co.call.impl.viewmodel.MainViewModel
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val backStack = rememberNavBackStack(HomeNavKey)

    // 백스택 top에서 현재 탭을 derive — 뒤로가기 시에도 자동 동기화
    val currentTab = backStack.lastOrNull()?.toMainTab() ?: MainTab.HOME

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.NavigateTo -> {
                backStack.clear()
                backStack.add(HomeNavKey)
                if (sideEffect.tab != MainTab.HOME) {
                    backStack.add(sideEffect.tab.toNavKey())
                }
            }
        }
    }

    // TODO: 현재 화면에 따라 BottomBar 표시 여부 결정.
    val showBottomBar = true

    MainScreenContent(
        currentTab = currentTab,
        onTabSelected = { viewModel.handleIntent(MainIntent.SelectTab(it)) },
        showBottomBar = showBottomBar,
        modifier = modifier
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            entryProvider = entryProvider {
                homeEntry()
                chattingEntry()
                myPageEntry()
            }
        )
    }
}

@Composable
internal fun MainScreenContent(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    showBottomBar: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected,
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    CallFromAiTheme {
        MainScreenContent(
            currentTab = MainTab.HOME,
            onTabSelected = {},
            showBottomBar = true,
        ) {
            // Preview content
        }
    }
}

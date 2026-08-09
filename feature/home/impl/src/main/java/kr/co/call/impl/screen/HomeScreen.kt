package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import java.time.LocalDateTime
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.impl.component.dialog.CallConnectDialog
import kr.co.call.impl.component.dialog.CharacterChangeConfirmDialog
import kr.co.call.impl.component.dialog.CharacterChangeDialog
import kr.co.call.impl.component.dialog.CharacterChangeUnavailableDialog
import kr.co.call.impl.component.dialog.NonMainCharacterCallDialog
import kr.co.call.impl.component.header.HomeHeader
import kr.co.call.impl.component.history.CallHistoryList
import kr.co.call.impl.component.history.HomeHistorySection
import kr.co.call.impl.component.history.NotificationListHeader
import kr.co.call.impl.component.history.notificationItems
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.HomeIntent
import kr.co.call.impl.viewmodel.HomeSideEffect
import kr.co.call.impl.viewmodel.HomeViewModel
import kr.co.call.impl.viewmodel.state.HomeDialogState
import kr.co.call.impl.viewmodel.state.HomeState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 홈 화면 진입점
 * - ViewModel 상태와 SideEffect 구독
 * - 화면 이동 이벤트를 상위 내비게이터로 전달
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToCharacterOnboarding: () -> Unit = {},
    onNavigateToCall: (characterId: Long, characterName: String, characterImageUrl: String?) -> Unit = { _, _, _ -> },
    onNavigateToCallRecord: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    // 다른 화면에서 돌아올 때 최신 데이터로 갱신
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.handleIntent(HomeIntent.OnResume)
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            HomeSideEffect.NavigateToCharacterOnboarding -> {
                onNavigateToCharacterOnboarding()
            }

            is HomeSideEffect.NavigateToCall -> {
                onNavigateToCall(
                    sideEffect.characterId,
                    sideEffect.characterName,
                    sideEffect.characterImageUrl,
                )
            }

            is HomeSideEffect.NavigateToCallRecord -> {
                onNavigateToCallRecord(sideEffect.callId)
            }

            is HomeSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    HomeScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

/**
 * 홈 화면 UI
 * - 상태를 화면에 표시
 * - 사용자 행동을 Intent로 전달
 */
@Composable
private fun HomeScreenContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeContent(
        state = state,
        onIntent = onIntent,
        modifier = modifier,
    )

    HomeDialogs(
        state = state,
        onIntent = onIntent,
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background),
    ) {
        item {
            HomeHeader(
                summary = state.summary,
                hasUnreadNotification = state.hasUnreadNotification,
                onCharacterChangeClick = {
                    onIntent(HomeIntent.Character.ClickChange)
                },
                onNotificationClick = {
                    onIntent(HomeIntent.History.ClickNotificationShortcut)
                },
                onCallClick = {
                    onIntent(HomeIntent.Call.ClickMain)
                },
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .background(CallTheme.colors.gray100),
            )
        }

        item {
            HomeHistorySection(
                selectedTab = state.selectedHistoryTab,
                onTabClick = { tab ->
                    onIntent(HomeIntent.History.SelectTab(tab))
                },
            )
        }

        when (state.selectedHistoryTab) {
            HomeHistoryTab.NOTIFICATION -> {
                item {
                    NotificationListHeader()
                }
                notificationItems(
                    notifications = state.notifications,
                    onCallClick = { characterId, characterName ->
                        onIntent(
                            HomeIntent.Call.ClickNotification(
                                characterId = characterId,
                                characterName = characterName,
                            ),
                        )
                    },
                )
            }

            HomeHistoryTab.CALL_HISTORY -> {
                item {
                    CallHistoryList(
                        histories = state.callHistories,
                        onRecordClick = { callId ->
                            onIntent(HomeIntent.History.ClickRecord(callId))
                        },
                    )
                }
            }
        }
    }
}

/**
 * 다이얼로그를 표시하는 UI
 */
@Composable
private fun HomeDialogs(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    when (val dialogState = state.dialogState) {
        HomeDialogState.CharacterSelection -> {
            CharacterChangeDialog(
                characters = state.characters,
                onCharacterClick = { characterId, characterName ->
                    onIntent(
                        // 선택한 캐릭터로 변경
                        HomeIntent.Character.Select(
                            characterId = characterId,
                            characterName = characterName,
                        ),
                    )
                },
                onAddCharacterClick = {
                    // 캐릭터 선택
                    onIntent(HomeIntent.Character.ClickAdd)
                },
                onDismissRequest = {
                    // 취소 버튼 클릭
                    onIntent(HomeIntent.DismissDialog)
                },
            )
        }

        HomeDialogState.CharacterChangeUnavailable -> {
            CharacterChangeUnavailableDialog(
                onConfirmClick = {
                    onIntent(HomeIntent.DismissDialog)
                },
                onDismissRequest = {
                    onIntent(HomeIntent.DismissDialog)
                },
            )
        }

        // 캐릭터 변경 팝업일 경우
        is HomeDialogState.CharacterChangeConfirmation -> {
            CharacterChangeConfirmDialog(
                characterName = dialogState.characterName,
                // 확인 버튼 클릭
                onConfirmClick = {
                    onIntent(HomeIntent.Character.ConfirmChange)
                },
                // 취소 버튼 클릭
                onDismissRequest = {
                    onIntent(HomeIntent.DismissDialog)
                },
            )
        }

        is HomeDialogState.CallConfirmation -> {
            CallConnectDialog(
                characterName = dialogState.characterName,
                onConnectClick = {
                    onIntent(HomeIntent.Call.Confirm)
                },
                onDismissRequest = {
                    onIntent(HomeIntent.DismissDialog)
                },
            )
        }

        is HomeDialogState.NonMainCharacterCall -> {
            NonMainCharacterCallDialog(
                characterName = dialogState.characterName,
                onConfirmClick = {
                    onIntent(HomeIntent.DismissDialog)
                },
                onDismissRequest = {
                    onIntent(HomeIntent.DismissDialog)
                },
            )
        }

        null -> Unit
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun HomeScreenPreview() {
    val notifications = remember {
        createHomeNotificationMockData(
            now = LocalDateTime.of(2026, 7, 17, 15, 0),
        )
    }
    var state by remember {
        mutableStateOf(
            HomeState(
                summary = HomeSummary(
                    firstName = "수현",
                    relationshipDays = 30,
                    totalCallCount = 24,
                    callStreakDays = 12,
                ),
                notifications = notifications,
                hasUnreadNotification = notifications.any { notification ->
                    !notification.isRead
                },
                selectedHistoryTab = HomeHistoryTab.NOTIFICATION,
            ),
        )
    }

    CallFromAiTheme {
        HomeScreenContent(
            state = state,
            onIntent = { intent ->
                if (intent is HomeIntent.History.SelectTab) {
                    state = state.copy(selectedHistoryTab = intent.tab)
                }
            },
        )
    }
}

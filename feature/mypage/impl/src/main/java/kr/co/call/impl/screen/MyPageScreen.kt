package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.ProfileCard
import kr.co.call.impl.component.SettingsMenuContent
import kr.co.call.impl.component.SettingsSectionCard
import kr.co.call.impl.viewmodel.MyPageIntent
import kr.co.call.impl.viewmodel.MyPageSideEffect
import kr.co.call.impl.viewmodel.MyPageState
import kr.co.call.impl.viewmodel.MyPageViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showComingSoon by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToProfileDetail -> { /* TODO: 네비게이션 */ }
            is MyPageSideEffect.NavigateToChargeTicket -> {  showComingSoon = true }
            is MyPageSideEffect.NavigateToPurchaseTicket -> { showComingSoon = true }
            is MyPageSideEffect.NavigateToTicketHistory -> { showComingSoon = true  }
            is MyPageSideEffect.NavigateToCharacterManagement -> { /* TODO */ }
            is MyPageSideEffect.NavigateToFaq -> { /* TODO */ }
            is MyPageSideEffect.NavigateToInquiry -> {  showComingSoon = true }
            is MyPageSideEffect.NavigateToTerms -> { /* TODO */ }
            is MyPageSideEffect.ShowLogoutConfirmDialog -> { showLogoutDialog = true }
            is MyPageSideEffect.ShowDeleteAccountConfirmDialog -> { showDeleteAccountDialog=true }
            is MyPageSideEffect.NavigateToLogin -> { showLogoutDialog = false /*TODO: 로그인 화면으로 */}
            is MyPageSideEffect.NavigateToLanding -> { showDeleteAccountDialog = false /*TODO: 랜딩 화면으로*/}
        }
    }

    MyPageScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )

    // 로그아웃 다이얼로그
    if (showLogoutDialog) {
        MyPageLogoutDialog(
            onConfirm = { viewModel.handleIntent(MyPageIntent.ConfirmLogout) },
            onDismiss = { showLogoutDialog = false },
        )
    }

    // 계정 삭제 다이얼로그
    if (showDeleteAccountDialog) {
        MyPageDeleteAccountDialog(
            onConfirm = { viewModel.handleIntent(MyPageIntent.ConfirmDeleteAccount) },
            onDismiss = { showDeleteAccountDialog = false },
        )
    }

    // 준비중 화면 다이얼로그
    if (showComingSoon) {
        ComingSoonScreen(onBackClick = { showComingSoon = false })
    }
}

@Composable
private fun MyPageScreenContent(
    state: MyPageState,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .background(CallTheme.colors.background),
        ) {
        Box{
            // 배경 핑크 박스
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CallTheme.colors.mainVariant3),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                Spacer(modifier = Modifier.height(80.dp))

                // 상단 프로필 카드
                ProfileCard(
                    profileImageUrl = state.profileImageUrl,
                    nickname = state.nickname,
                    tier = state.tier,
                    remainingTicketCount = state.remainingTicketCount,
                    onProfileClick = { onIntent(MyPageIntent.ClickProfile) },
                    onChargeClick = { onIntent(MyPageIntent.ClickChargeTicket) },
                    onPurchaseClick = { onIntent(MyPageIntent.ClickPurchaseTicket) },
                    onHistoryClick = { onIntent(MyPageIntent.ClickHistoryTicket) },
                )

                // 메뉴 - 캐릭터 관리
                SettingsSectionCard(
                    items = listOf(
                        {
                            SettingsMenuContent(
                                icon = "\uD83D\uDC64",
                                label = "캐릭터 관리",
                                onClick = { onIntent(MyPageIntent.ClickCharacterManagement) },
                            )
                        },
                    )
                )

                // 메뉴 - 고객 지원
                SettingsSectionCard(
                    title = "고객 지원",
                    items = listOf(
                        {
                            SettingsMenuContent(
                                icon = "❓",
                                label = "자주 하는 질문",
                                onClick = { onIntent(MyPageIntent.ClickFaq) },
                            )
                        },
                        {
                            SettingsMenuContent(
                                icon = "✉️",
                                label = "문의하기",
                                onClick = { onIntent(MyPageIntent.ClickInquiry) },
                            )
                        },
                        {
                            SettingsMenuContent(
                                icon = "📄",
                                label = "약관",
                                onClick = { onIntent(MyPageIntent.ClickTerms) },
                            )
                        },
                        {
                            SettingsMenuContent(
                                icon = "ℹ️", label = "버전 정보", onClick = { },
                                trailing = {
                                    Text(
                                        text = "v${state.appVersion}",
                                        style = CallTheme.typography.bodySmall,
                                        color = CallTheme.colors.gray400
                                    )
                                })
                        }
                    )
                )

                // 메뉴 - 기타
                SettingsSectionCard(
                    title = "기타",
                    items = listOf(
                        {
                            SettingsMenuContent(
                                icon = "\uD83D\uDEAA",
                                label = "로그아웃",
                                onClick = { onIntent(MyPageIntent.ClickLogout) })
                        },
                        {
                            SettingsMenuContent(
                                icon = "⛔",
                                label = "계정 삭제",
                                onClick = { onIntent(MyPageIntent.ClickDeleteAccount) })
                        },
                    )
                )
            }

            if (state.loadStatus == LoadStatus.Loading) {
                // TODO: 로딩 인디케이터 오버레이
            }

        }
    }

}

// 로그아웃 다이얼로그 컴포넌트
@Composable
private fun MyPageLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TwoButtonPopup(
        label = "로그아웃",
        title = "로그아웃 하시겠습니까?",
        positiveText = "확인",
        negativeText = "취소",
        onPositiveClick = onConfirm,
        onNegativeClick = onDismiss,
        onDismissRequest = onDismiss,
    )
}

// 계정삭제 다이얼로그 컴포넌트
@Composable
private fun MyPageDeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TwoButtonPopup(
        label = "",
        title = "잠시만요 🖐️",
        description = buildAnnotatedString {
            append("탈퇴 시 계정 및 이용 기록은 모두 삭제되며,\n삭제된 데이터는 복구가 불가능합니다.\n\n탈퇴를 진행할까요?")
        },
        positiveText = "탈퇴할게요",
        negativeText = "취소",
        onPositiveClick = onConfirm,
        onNegativeClick = onDismiss,
        onDismissRequest = onDismiss,
    )
}

// preview용 state
private val previewMyPageState = MyPageState(
    nickname = "김수현",
    tier = "Basic",
    remainingTicketCount = 18,
    appVersion = "1.0.0",
)

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    CallFromAiTheme {
        MyPageScreenContent(
            state = previewMyPageState,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "로딩 상태")
@Composable
private fun MyPageScreenLoadingPreview() {
    CallFromAiTheme {
        MyPageScreenContent(
            state = MyPageState(loadStatus = LoadStatus.Loading),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "로그아웃")
@Composable
private fun MyPageScreenLogoutDialogPreview() {
    CallFromAiTheme {
            MyPageScreenContent(
                state = previewMyPageState,
                onIntent = {},
            )
            TwoButtonPopup(
                label = "로그아웃",
                title = "로그아웃 하시겠습니까?",
                positiveText = "확인",
                negativeText = "취소",
                onPositiveClick = {},
                onNegativeClick = {},
            )
    }
}

@Preview(showBackground = true, name = "계정 삭제")
@Composable
private fun MyPageScreenDeleteAccountDialogPreview() {
    CallFromAiTheme {
            MyPageScreenContent(
                state = previewMyPageState,
                onIntent = {}
            )
            TwoButtonPopup(
                label = "",
                title = "잠시만요 🖐️",
                description = buildAnnotatedString {
                    append("탈퇴 시 계정 및 이용 기록은 모두 삭제되며,\n삭제된 데이터는 복구가 불가능합니다.\n\n탈퇴를 진행할까요?")
                },
                positiveText = "탈퇴할게요",
                negativeText = "취소",
                onPositiveClick = {},
                onNegativeClick = {},
                onDismissRequest = {}
            )
    }
}
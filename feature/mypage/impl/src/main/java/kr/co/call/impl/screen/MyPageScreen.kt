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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToProfileDetail -> { /* TODO: 네비게이션 */ }
            is MyPageSideEffect.NavigateToChargeTicket -> { /* TODO */ }
            is MyPageSideEffect.NavigateToPurchaseTicket -> { /* TODO */ }
            is MyPageSideEffect.NavigateToTicketHistory -> { /* TODO */ }
            is MyPageSideEffect.NavigateToCharacterManagement -> { /* TODO */ }
            is MyPageSideEffect.NavigateToFaq -> { /* TODO */ }
            is MyPageSideEffect.NavigateToInquiry -> { /* TODO */ }
            is MyPageSideEffect.NavigateToTerms -> { /* TODO */ }
            is MyPageSideEffect.ShowLogoutConfirmDialog -> { /* TODO: 다이얼로그 표시 */ }
            is MyPageSideEffect.ShowDeleteAccountConfirmDialog -> { /* TODO: 다이얼로그 표시 */ }
            is MyPageSideEffect.NavigateToLogin -> { /* TODO: 로그인 화면으로 */ }
            is MyPageSideEffect.NavigateToLanding -> {/*TODO: 랜딩 화면으로 */}
        }
    }

    MyPageScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
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

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    CallFromAiTheme {
        MyPageScreenContent(
            state = MyPageState(
                nickname = "김수현",
                tier = "Basic",
                remainingTicketCount = 18,
                appVersion = "1.0.0",
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun MyPageScreenLoadingPreview() {
    CallFromAiTheme {
        MyPageScreenContent(
            state = MyPageState(loadStatus = LoadStatus.Loading),
            onIntent = {},
        )
    }
}
package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.SettingsMenuContent
import kr.co.call.impl.component.SettingsSectionCard
import kr.co.call.impl.viewmodel.ProfileIntent
import kr.co.call.impl.viewmodel.ProfileSideEffect
import kr.co.call.impl.viewmodel.ProfileState
import kr.co.call.impl.viewmodel.ProfileViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.NavigateToEditProfile -> { /* TODO: 내 정보 수정 화면 */ }
            is ProfileSideEffect.NavigateToSubscription -> { /* TODO: 구독 관리 화면 */ }
            is ProfileSideEffect.NavigateToDoNotDisturbTime -> { /* TODO: 방해 금지 시간 화면 */ }
            is ProfileSideEffect.NavigateToCallTimeManagement -> { /* TODO: 전화 시간 관리 화면 */ }
        }
    }

    ProfileScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun ProfileScreenContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(CallTheme.colors.background),
    ) {
        // 상단 앱바
        CommonTopAppBar(onBackClick = onBackClick, title = "프로필")

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            // 프로필 요약 헤더
            ProfileDetailHeader(
                profileImageUrl = state.profileImageUrl,
                nickname = state.nickname,
                onEditClick = { onIntent(ProfileIntent.ClickEditProfile) },
            )

            // 메뉴 - 구독 관리
            SettingsSectionCard(
                title = "구독 관리",
                items = listOf(
                    {
                        SettingsMenuContent(
                            icon = "\uD83D\uDCB3",
                            label = "구독",
                            onClick = { onIntent(ProfileIntent.ClickSubscription) },
                        )
                    },
                )
            )

            // 메뉴 - 알림 설정
            SettingsSectionCard(
                title = "알림 설정",
                items = listOf(
                    {
                        SettingsMenuContent(
                            icon = "\uD83D\uDD14",
                            label = "전체 알림",
                            onClick = {},
                            trailing = {
                                Switch(
                                    checked = state.isAllNotificationEnabled,
                                    onCheckedChange = { onIntent(ProfileIntent.ToggleAllNotification(it)) },
                                    colors = notificationSwitchColors(),
                                )
                            },
                        )
                    },
                    {
                        SettingsMenuContent(
                            icon = "\uD83C\uDF19",
                            label = "심야 통화 허용",
                            onClick = {},
                            trailing = {
                                Switch(
                                    checked = state.isLateNightCallAllowed,
                                    onCheckedChange = { onIntent(ProfileIntent.ToggleLateNightCall(it)) },
                                    colors = notificationSwitchColors(),
                                )
                            },
                        )
                    },
                    {
                        SettingsMenuContent(
                            icon = "\uD83D\uDEAB",
                            label = "방해 금지 시간",
                            onClick = { onIntent(ProfileIntent.ClickDoNotDisturbTime) },
                            trailing = { TimeRangeTrailing(text = state.doNotDisturbTimeText) },
                        )
                    },
                    {
                        SettingsMenuContent(
                            icon = "\u260E\uFE0F",
                            label = "전화 시간 관리",
                            onClick = { onIntent(ProfileIntent.ClickCallTimeManagement) },
                        )
                    },
                )
            )
        }
    }
}

// 프로필 요약 헤더 컴포넌트
@Composable
private fun ProfileDetailHeader(
    profileImageUrl: String,
    nickname: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        // 프로필 사진
        AsyncImage(
            model = profileImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.img_mypage_profile_default),
            error = painterResource(id = R.drawable.img_mypage_profile_default),
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 사용자 이름
        Text(
            text = nickname,
            style = CallTheme.typography.titleSmallBold,
            color = CallTheme.colors.black,
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 내 정보 수정 버튼
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(CallTheme.colors.mainVariant5Chat)
                .clickable(onClick = onEditClick)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "내 정보 수정",
                style = CallTheme.typography.bodySmallBold,
                color = CallTheme.colors.mainVariant1,
            )
        }

        Spacer(modifier = Modifier.height(11.dp))
    }
}

// 방해 금지 시간 + 바로가기 버튼(>)
@Composable
private fun TimeRangeTrailing(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.gray400,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_mypage_arrow_right),
            contentDescription = "바로가기",
        )
    }
}

@Composable
private fun notificationSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = CallTheme.colors.white,
    checkedTrackColor = CallTheme.colors.mainVariant3,
    uncheckedThumbColor = CallTheme.colors.white,
    uncheckedTrackColor = CallTheme.colors.gray200,
)

// preview용 state
private val previewProfileState = ProfileState(
    nickname = "김수현",
    isAllNotificationEnabled = true,
    isLateNightCallAllowed = false,
)

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    CallFromAiTheme {
        ProfileScreenContent(
            state = previewProfileState,
            onIntent = {},
            onBackClick = {},
        )
    }
}
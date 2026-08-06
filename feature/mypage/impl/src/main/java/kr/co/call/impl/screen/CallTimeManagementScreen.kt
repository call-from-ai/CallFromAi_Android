package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.LocalBottomBarPadding
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.viewmodel.CallTimeManagementIntent
import kr.co.call.impl.viewmodel.CallTimeManagementSideEffect
import kr.co.call.impl.viewmodel.CallTimeManagementState
import kr.co.call.impl.viewmodel.CallTimeManagementViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CallTimeManagementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CallTimeManagementViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CallTimeManagementSideEffect.NavigateBack -> onBackClick()
            is CallTimeManagementSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    CallTimeManagementScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun CallTimeManagementScreenContent(
    state: CallTimeManagementState,
    onIntent: (CallTimeManagementIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomBarPadding = LocalBottomBarPadding.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 상단 탭 바
        CommonTopAppBar(
            title = "전화 오는 시간",
            onBackClick = { onIntent(CallTimeManagementIntent.ClickBack) },
        )

        when (state.loadStatus) {
            is LoadStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CallTheme.colors.mainVariant1)
                }
            }
            else -> {
                // 제목
                Text(
                    text = "언제 먼저 통화를\n걸어드리면 좋을까요?",
                    style = CallTheme.typography.titleSmallBold,
                    color = CallTheme.colors.black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp, bottom = 20.dp),
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(CallTheme.colors.subGray2),
                ) {
                    // PreferTime 컴포넌트 (오전시간대,낮시간대, ...)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(top = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        PreferTime.entries.forEach { option ->
                            PreferTime(
                                preferTime = option,
                                selected = state.selected == option,
                                onClick = {
                                    onIntent(CallTimeManagementIntent.SelectPreferTime(option))
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 완료 버튼
                    SecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = bottomBarPadding, top = 8.dp),
                        text = if (state.isSaving) "저장 중..." else "완료",
                        enabled = state.canComplete,
                        onClick = { onIntent(CallTimeManagementIntent.ClickComplete) },
                    )
                }
            }
        }
    }
}

/**
 * Preview
 */
@Preview(showBackground = true)
@Composable
private fun CallTimeManagementScreenPreview() {
    CallFromAiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CallTheme.colors.background)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            CommonTopAppBar(
                title = "전화 오는 시간",
                onBackClick = {},
            )
            Text(
                text = "언제 먼저 통화를\n걸어드리면 좋을까요?",
                style = CallTheme.typography.titleSmallBold,
                color = CallTheme.colors.black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 20.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(CallTheme.colors.subGray2),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PreviewPreferTimePlaceholder(
                        title = "오전 시간대",
                        time = "08:00 ~ 12:00",
                        selected = false,
                    )
                    PreviewPreferTimePlaceholder(
                        title = "낮 시간대",
                        time = "13:00 ~ 18:00",
                        selected = true,
                    )
                    PreviewPreferTimePlaceholder(
                        title = "늦은 오후 시간대",
                        time = "19:00 ~ 24:00",
                        selected = false,
                    )
                    PreviewPreferTimePlaceholder(
                        title = "언제든 좋아요",
                        time = null,
                        selected = false,
                    )
                }
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp, top = 8.dp),
                    text = "완료",
                    enabled = true,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun PreviewPreferTimePlaceholder(
    title: String,
    time: String?,
    selected: Boolean,
) {
    val background = if (selected) {
        CallTheme.colors.mainVariant2
    } else {
        CallTheme.colors.white
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(93.dp)
            .background(
                color = background,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 17.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (time != null) "$title  $time" else title,
            style = CallTheme.typography.bodyMediumMedium,
            color = CallTheme.colors.black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "시간대 설명",
            style = CallTheme.typography.caption,
            color = CallTheme.colors.gray600,
        )
    }
}

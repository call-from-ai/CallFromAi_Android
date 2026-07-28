package kr.co.call.impl.screen

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalTime
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.DisturbTimePickerBottomSheet
import kr.co.call.impl.viewmodel.DisturbTimeIntent
import kr.co.call.impl.viewmodel.DisturbTimeSideEffect
import kr.co.call.impl.viewmodel.DisturbTimeSheetType
import kr.co.call.impl.viewmodel.DisturbTimeState
import kr.co.call.impl.viewmodel.DisturbTimeViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DisturbTimeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DisturbTimeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DisturbTimeSideEffect.NavigateBack -> onBackClick()
            is DisturbTimeSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    DisturbTimeScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun DisturbTimeScreenContent(
    state: DisturbTimeState,
    onIntent: (DisturbTimeIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 상단 앱 바
            CommonTopAppBar(
                title = "방해 금지 시간",
                onBackClick = onBackClick,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // 본문
                Text(
                    text = "방해 금지 시간을\n설정해 주세요.",
                    style = CallTheme.typography.titleSmallBold,
                    color = CallTheme.colors.black,
                )

                Spacer(modifier = Modifier.height(77.dp))

                // 시작시간/종료시간 드롭다운
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(55.dp),
                ) {
                    TimeField(
                        label = "시작 시간",
                        time = state.startTime,
                        onClick = { onIntent(DisturbTimeIntent.ClickStartTime) },
                        modifier = Modifier.weight(1f),
                    )
                    TimeField(
                        label = "종료 시간",
                        time = state.endTime,
                        onClick = { onIntent(DisturbTimeIntent.ClickEndTime) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 하단 '완료' 버튼
            SecondaryButton(
                text = "완료",
                onClick = { onIntent(DisturbTimeIntent.ClickComplete) },
                enabled = state.canComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // 시작/종료 시간 선택 BottomSheet
        when (state.openSheet) {
            DisturbTimeSheetType.Start -> {
                DisturbTimePickerBottomSheet(
                    title = "시작 시간",
                    selectedTime = state.draftTime,
                    onTimeSelected = { onIntent(DisturbTimeIntent.SelectDraftTime(it)) },
                    onConfirmClick = { onIntent(DisturbTimeIntent.ConfirmSheet) },
                    onDismissRequest = { onIntent(DisturbTimeIntent.DismissSheet) },
                )
            }
            DisturbTimeSheetType.End -> {
                DisturbTimePickerBottomSheet(
                    title = "종료 시간",
                    selectedTime = state.draftTime,
                    onTimeSelected = { onIntent(DisturbTimeIntent.SelectDraftTime(it)) },
                    onConfirmClick = { onIntent(DisturbTimeIntent.ConfirmSheet) },
                    onDismissRequest = { onIntent(DisturbTimeIntent.DismissSheet) },
                )
            }
            null -> Unit
        }
    }
}

// 시작시간/종료시간 컴포넌트
@Composable
private fun TimeField(
    label: String,
    time: LocalTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = CallTheme.typography.bodyMedium,
            color = CallTheme.colors.gray600,
            modifier = Modifier.padding(bottom = 7.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CallTheme.colors.gray100)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatDisturbTimeLabel(time),
                style = CallTheme.typography.bodyMedium,
                color = CallTheme.colors.black,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_mypage_arrow_right),
                contentDescription = null,
                tint = CallTheme.colors.gray600,
                modifier = Modifier
                    .width(12.dp)
                    .rotate(90f),
            )
        }
    }
}

// 본문 필드 표시 (09:00 -> 09시, 09:30 -> 09시 30분)
private fun formatDisturbTimeLabel(time: LocalTime): String {
    return if (time.minute == 0) {
        String.format("%02d시", time.hour)
    } else {
        String.format("%02d시 %02d분", time.hour, time.minute)
    }
}

@Preview(showBackground = true)
@Composable
private fun DisturbTimeScreenPreview() {
    CallFromAiTheme {
        DisturbTimeScreenContent(
            state = DisturbTimeState(),
            onIntent = {},
            onBackClick = {},
        )
    }
}


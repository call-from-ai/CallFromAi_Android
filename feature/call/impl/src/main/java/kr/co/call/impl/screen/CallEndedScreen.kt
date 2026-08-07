package kr.co.call.impl.screen

import java.time.LocalDateTime
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallRecordStatus
import kr.co.call.impl.component.CallDuration
import kr.co.call.impl.component.CallGradientBackground
import kr.co.call.impl.component.CallHeader
import kr.co.call.impl.component.ScreenBlocker
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState

@Composable
fun CallEndedScreen(
    state: CallState,
    character: CallCharacterUiModel,
    modifier: Modifier = Modifier,
) {
    CallGradientBackground(modifier = modifier) {
        ScreenBlocker(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            CallHeader(isIncoming = state.isIncoming)
            Spacer(modifier = Modifier.height(13.dp))
            CallDuration(
                durationSeconds = state.durationSeconds,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = character.name,
                color = CallTheme.colors.gray900,
                style = CallTheme.typography.titleSuperBig,
            )
            Spacer(modifier = Modifier.height(70.dp))
            Text(
                text = if (state.phase == CallPhase.ERROR) {
                    "통화를 종료하지 못했어요"
                } else {
                    "통화가 종료되었어요"
                },
                color = CallTheme.colors.gray900,
                style = CallTheme.typography.bodyLargeBold,
            )
            CallSummaryText(callInfo = state.callInfo)
        }
    }
}

/**
 * 통화 요약 텍스트. summaryStatus가 READY일 때만 요약을,
 * PROCESSING이면 준비 중 안내를 보여주고 그 외(NONE·FAILED·null)엔 아무것도 표시하지 않음
 */
@Composable
private fun CallSummaryText(
    callInfo: CallInfo?,
    modifier: Modifier = Modifier,
) {
    val summaryText = when (callInfo?.summaryStatus) {
        CallRecordStatus.READY -> callInfo.title
        CallRecordStatus.PROCESSING -> "통화 요약을 준비하고 있어요"
        else -> null
    } ?: return

    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = summaryText,
        modifier = modifier,
        color = CallTheme.colors.gray600,
        style = CallTheme.typography.bodyMedium,
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallEndedScreenPreview() {
    CallFromAiTheme {
        CallEndedScreen(
            state = CallState(
                isIncoming = true,
                phase = CallPhase.ENDED,
                durationSeconds = 15 * 60 + 38,
                callInfo = CallInfo(
                    callId = 1L,
                    title = "출근 준비와 아침 일정 이야기",
                    calledAt = LocalDateTime.now(),
                    characterName = "민준",
                    recordingUrl = null,
                    durationMillis = 0L,
                    summaryStatus = CallRecordStatus.READY,
                    recordingStatus = CallRecordStatus.READY,
                ),
            ),
            character = CallCharacterUiModel(name = "민준"),
        )
    }
}

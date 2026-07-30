package kr.co.call.impl.screen

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
import kr.co.call.impl.component.CallDuration
import kr.co.call.impl.component.CallGradientBackground
import kr.co.call.impl.component.CallHeader
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.model.CallDirection
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState

@Composable
fun CallEndedScreen(
    state: CallState,
    modifier: Modifier = Modifier,
) {
    CallGradientBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            CallHeader(direction = state.direction)
            Spacer(modifier = Modifier.height(13.dp))
            CallDuration(
                durationSeconds = state.endedDurationSeconds ?: state.durationSeconds,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.character.name,
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
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallEndedScreenPreview() {
    CallFromAiTheme {
        CallEndedScreen(
            state = CallState(
                character = CallCharacterUiModel(name = "민준"),
                direction = CallDirection.INCOMING,
                phase = CallPhase.ENDED,
                durationSeconds = 15 * 60 + 38,
                endedDurationSeconds = 15 * 60 + 38,
            ),
        )
    }
}

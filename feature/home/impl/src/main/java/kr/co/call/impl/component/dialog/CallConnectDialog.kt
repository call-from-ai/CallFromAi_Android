package kr.co.call.impl.component.dialog

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme

@Composable
fun CallConnectDialog(
    characterName: String,
    onConnectClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TwoButtonPopup(
        label = "통화 연결",
        title = "${characterName}에게 바로\n통화를 연결할까요?",
        positiveText = "연결",
        negativeText = "취소",
        onPositiveClick = onConnectClick,
        onNegativeClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
        modifier = modifier.width(321.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun CallConnectDialogPreview() {
    CallFromAiTheme {
        CallConnectDialog(
            characterName = "민준",
            onConnectClick = {},
            onDismissRequest = {},
        )
    }
}

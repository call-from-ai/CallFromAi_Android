package kr.co.call.designsystem.component.popup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 버튼 2개가 있는 팝업
 */
@Composable
fun TwoButtonPopup(
    modifier: Modifier = Modifier,
    label: String,
    title: String,
    positiveText: String,
    negativeText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    description: AnnotatedString? = null,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PopupCard(label = label, title = title, description = description, modifier = modifier) {
            PrimaryButton(text = positiveText, onClick = onPositiveClick)
            PrimaryButton(
                text = negativeText,
                onClick = onNegativeClick,
                containerColor = CallTheme.colors.gray200,
                contentColor = CallTheme.colors.gray600,
                pressedContainerColor = CallTheme.colors.gray400,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TwoButtonPopupPreview() {
    CallFromAiTheme {
        TwoButtonPopup(
            label = "통화 연결",
            title = "민준에게 바로\n통화를 연결할까요?",
            positiveText = "연결",
            negativeText = "취소",
            onPositiveClick = {},
            onNegativeClick = {},
            onDismissRequest = {},
        )
    }
}

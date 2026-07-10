package kr.co.call.designsystem.component.popup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 버튼 1개가 있는 팝업창
 */
@Composable
fun OneButtonPopup(
    modifier: Modifier = Modifier,
    label: String,
    title: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    description: AnnotatedString? = null,
    onDismissRequest: () -> Unit = onButtonClick,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PopupCard(label = label, title = title, description = description, modifier = modifier) {
            PrimaryButton(text = buttonText, onClick = onButtonClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OneButtonPopupPreview() {
    CallFromAiTheme {
        OneButtonPopup(
            label = "캐릭터 변경하기",
            title = "아직 캐릭터를\n변경할 수 없어요",
            buttonText = "확인",
            onButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OneButtonPopupWithDescriptionPreview() {
    CallFromAiTheme {
        OneButtonPopup(
            label = "캐릭터 추가하기",
            title = "아직 캐릭터를\n추가할 수 없어요",
            description = buildAnnotatedString {
                append("새로운 캐릭터를 만든 후 ")
                withStyle(SpanStyle(color = CallTheme.colors.mainVariant1)) {
                    append("24시간")
                }
                append("이 지나면\n또 다른 캐릭터를 만들 수 있어요")
            },
            buttonText = "확인",
            onButtonClick = {},
        )
    }
}

package kr.co.call.impl.component.dialog

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun CharacterChangeConfirmDialog(
    characterName: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TwoButtonPopup(
        label = "메인 연인 교체하기",
        title = "${characterName}로 메인을 교체하시겠습니까?",
        description = buildAnnotatedString {
            append("전화는 선택한 연인과만 가능하며,\n연인 교체 후 ")
            withStyle(SpanStyle(color = CallTheme.colors.mainVariant1)) {
                append("최소 3일")
            }
            append("이 지나야\n다시 교체할 수 있습니다.")
        },
        positiveText = "확인",
        negativeText = "취소",
        onPositiveClick = onConfirmClick,
        onNegativeClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
        modifier = modifier.width(321.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun CharacterChangeConfirmDialogPreview() {
    CallFromAiTheme {
        CharacterChangeConfirmDialog(
            characterName = "동휘",
            onConfirmClick = {},
            onDismissRequest = {},
        )
    }
}

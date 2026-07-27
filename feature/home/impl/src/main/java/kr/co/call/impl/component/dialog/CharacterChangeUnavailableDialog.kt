package kr.co.call.impl.component.dialog

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.popup.OneButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 캐릭터를 변경할 수 없는 상태를 안내하는 다이얼로그
 *
 * @param onConfirmClick 확인 버튼 클릭 콜백
 * @param onDismissRequest 바깥 영역 또는 뒤로가기로 닫을 때 호출되는 콜백
 */
@Composable
fun CharacterChangeUnavailableDialog(
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OneButtonPopup(
        label = "캐릭터 변경하기",
        title = "아직 캐릭터를\n변경할 수 없어요",
        description = buildAnnotatedString {
            append("새로운 캐릭터를 만든 후 ")
            withStyle(
                SpanStyle(color = CallTheme.colors.mainVariant1),
            ) {
                append("24시간")
            }
            append("이 지나면\n또 다른 캐릭터를 만들 수 있어요")
        },
        buttonText = "확인",
        onButtonClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier.width(321.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun CharacterChangeUnavailableDialogPreview() {
    CallFromAiTheme {
        CharacterChangeUnavailableDialog(
            onConfirmClick = {},
            onDismissRequest = {},
        )
    }
}

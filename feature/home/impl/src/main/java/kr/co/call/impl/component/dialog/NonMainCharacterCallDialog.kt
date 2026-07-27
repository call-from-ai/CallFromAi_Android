package kr.co.call.impl.component.dialog

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.popup.OneButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme

/**
 * 메인 연인이 아닌 캐릭터에게 전화를 걸었을 때 표시되는 다이얼로그
 * @param characterName 전화를 걸려고 했던 캐릭터 이름
 */
@Composable
fun NonMainCharacterCallDialog(
    characterName: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OneButtonPopup(
        label = "부재중 전화",
        title = "앗! 현재는 ${characterName}에게\n전화를 걸 수 없어요. 💦",
        description = buildAnnotatedString {
            append("전화는 메인 연인과만 이용할 수 있어요.\n")
            append("다른 캐릭터와 전화를 하고 싶다면\n")
            append("상단에서 연인 교체하기를 클릭해주세요.")
        },
        buttonText = "확인",
        onButtonClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier.width(321.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun NonMainCharacterCallDialogPreview() {
    CallFromAiTheme {
        NonMainCharacterCallDialog(
            characterName = "동휘",
            onConfirmClick = {},
            onDismissRequest = {},
        )
    }
}

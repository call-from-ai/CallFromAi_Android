package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.model.TextFieldState

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState(),
    onValueChange: (String) -> Unit = {},
    onCameraClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(
                color = CallTheme.colors.subGray,
                shape = RoundedCornerShape(50.dp),
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIcon(onClick = onCameraClick) {
            Icon(
                painter = painterResource(R.drawable.ic_home_alarm),
                contentDescription = "카메라",
                tint = Color.Unspecified,
            )
        }

        BasicTextField(
            value = state.text,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            textStyle = CallTheme.typography.bodySmall.copy(
                color = CallTheme.colors.black,
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (state.text.isEmpty()) {
                        androidx.compose.material3.Text(
                            text = "메시지를 입력하세요",
                            style = CallTheme.typography.bodySmall,
                            color = CallTheme.colors.gray400,
                        )
                    }
                    innerTextField()
                }
            }
        )

        CircleIcon(onClick = onSendClick) {
            Icon(
                painter = painterResource(R.drawable.ic_home_alarm),
                contentDescription = "전송",
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChatTextFieldPreview() {
    CallFromAiTheme {
        ChatTextField(
            state = TextFieldState(text = ""),
        )
    }
}

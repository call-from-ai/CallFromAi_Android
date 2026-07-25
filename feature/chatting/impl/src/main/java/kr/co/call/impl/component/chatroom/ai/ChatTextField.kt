package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Row(
        modifier = modifier
            .background(
                color = CallTheme.colors.subGray,
                shape = RoundedCornerShape(50.dp),
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIcon(
            painter = painterResource(R.drawable.ic_chat_camera),
            contentDescription = "카메라",
            onClick = onCameraClick,
        )

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
                        Text(
                            text = "메시지를 입력하세요",
                            style = CallTheme.typography.bodySmall,
                            color = CallTheme.colors.gray400,
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (isKeyboardVisible) {
            CircleIcon(
                painter = painterResource(R.drawable.ic_chat_send),
                contentDescription = "전송",
                onClick = onSendClick,
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_chat_gallery),
                contentDescription = "갤러리",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = (-12).dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCameraClick,
                    ),
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

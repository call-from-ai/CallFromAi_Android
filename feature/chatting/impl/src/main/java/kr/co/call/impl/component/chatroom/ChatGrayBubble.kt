package kr.co.call.impl.component.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatGrayBubble(
    text: AnnotatedString
) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = CallTheme.colors.gray100,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .padding(
                start = 23.dp,
                end = 21.dp,
                top = 9.dp,
                bottom = 9.dp
            ),
        style = CallTheme.typography.bodyMediumMedium.copy(
            color = CallTheme.colors.black
        )
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun ChatGrayBubblePreview() {
    CallFromAiTheme {
        ChatGrayBubble(
            text = AnnotatedString("안녕하세요! 오늘 날씨가 참 좋네요.")
        )
    }
}

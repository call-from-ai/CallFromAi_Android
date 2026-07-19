package kr.co.call.impl.component.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatPinkBubble(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                brush = CallTheme.colors.chatGradientReverse,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 0.dp
                )
            )
            .padding(
                start = 23.dp,
                end = 21.dp,
                top = 9.dp,
                bottom = 9.dp
            ),
        style = CallTheme.typography.bodyMediumMedium.copy(
            color = CallTheme.colors.white
        ),
        textAlign = TextAlign.End
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun ChatPinkBubblePreview() {
    CallFromAiTheme {
        ChatPinkBubble(
            text = "오늘 뭐하고 지냈어? 보고싶다!"
        )
    }
}

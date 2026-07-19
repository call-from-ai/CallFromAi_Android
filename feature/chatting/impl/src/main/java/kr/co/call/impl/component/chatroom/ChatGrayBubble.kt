package kr.co.call.impl.component.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatGrayBubble(
    text: AnnotatedString,
    time: String,
    isLoading: Boolean = false,
) {
    // Modifier는 bubbleModifier 변수로 분리해 두 분기가 공유하도록 함
    val bubbleModifier = Modifier
        .widthIn(max = 280.dp)
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
        )

    Row(verticalAlignment = Alignment.Bottom) {
        if (isLoading) {
            LoadingIndicator(modifier = bubbleModifier)
        } else {
            Text(
                text = text,
                modifier = bubbleModifier,
                style = CallTheme.typography.bodyMediumMedium.copy(
                    color = CallTheme.colors.black
                )
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = time,
            style = CallTheme.typography.caption.copy(
                color = CallTheme.colors.gray400
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun ChatGrayBubblePreview() {
    CallFromAiTheme {
        ChatGrayBubble(
            text = AnnotatedString("안녕하세요! 오늘 날씨가 참 좋네요."),
            time = "12:01"
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun ChatGrayBubbleLoadingPreview() {
    CallFromAiTheme {
        ChatGrayBubble(
            text = AnnotatedString(""),
            time = "12:01",
            isLoading = true
        )
    }
}

package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatLightPinkBubble(
    text: AnnotatedString,
    time: String
) {
    Row(verticalAlignment = Alignment.Bottom) {
        Box(
            modifier = Modifier
                .background(
                    color = CallTheme.colors.mainVariant5Chat,
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
        ) {
            Text(
                text = text,
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
private fun ChatLightPinkBubblePreview() {
    CallFromAiTheme {
        ChatLightPinkBubble(
            text = buildAnnotatedString {
                append("문의를 남겨주시면\n")
                withStyle(SpanStyle(color = CallTheme.colors.mainVariant1)) {
                    append("내용을 확인한 뒤 순차적으로 답변해 드릴게요!")
                }
            },
            time = "12:01"
        )
    }
}

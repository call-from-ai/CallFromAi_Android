package kr.co.call.impl.component.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatLightPinkBubble(
    text: String
) {
    Row(
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
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chat_telephone),
            contentDescription = "전화기 이미지",
            modifier = Modifier.size(width = 22.dp, height = 21.dp),
            tint = CallTheme.colors.mainVariant1
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = CallTheme.typography.bodyMediumMedium.copy(
                color = CallTheme.colors.black
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
            text = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요."
        )
    }
}

package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallColors
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ManagerChatField(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(50)
            )
            .height(44.dp)
            .background(
                color = CallTheme.colors.subGray,
                shape = RoundedCornerShape(50)
            )
            .padding(start = 50.dp, top = 12.dp, end = 12.dp)
    ) {
        Text(
            text = "메시지를 입력할 수 없습니다.",
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.gray600
        )

    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun ManagerChatFieldPreview() {
    CallFromAiTheme {
        ManagerChatField()
    }
}
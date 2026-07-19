package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ManagerChatTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    Row(
        modifier = modifier.background(CallTheme.colors.mainVariant5Chat)
            .padding(start = 17.dp, end = 17.dp, top = 6.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chat_back),
            contentDescription = "뒤로가기",
            tint = Color.Unspecified,
            modifier = Modifier.noRippleClickable(
                onClick = onBack
            )
        )

        Spacer(Modifier.width(14.dp))

        Image(
            painter = painterResource(R.drawable.ic_chat_manager),
            contentDescription = "매니저",
            modifier = Modifier.clip(CircleShape)
                .size(40.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = "전화왔어 매니저",
            style = CallTheme.typography.bodyMediumBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagerChatTopBar() {
    CallFromAiTheme {
        ManagerChatTopBar()
    }
}

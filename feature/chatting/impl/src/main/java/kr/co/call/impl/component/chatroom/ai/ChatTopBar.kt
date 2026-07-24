package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.ProfileImage
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.model.TopHeader

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    item: TopHeader = TopHeader(),
    onBack: () -> Unit = {},
    onCallClick: () -> Unit = {}
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

        ProfileImage(
            profileImageUrl = item.imgUrl,
            showCallBadge = true
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = item.name,
            style = CallTheme.typography.bodyMediumBold
        )

        Spacer(Modifier.width(10.dp))

        DDayText(
            text = item.dDay,
            modifier = Modifier.height(18.dp)
        )

        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.ic_chat_call),
            tint = Color.Unspecified,
            contentDescription = "전화기",
            modifier = Modifier.noRippleClickable {
                onCallClick()
            }
        )
    }


}

@Preview(showBackground = true)
@Composable
private fun ChatTopBarPreview() {
    CallFromAiTheme {
        ChatTopBar(
            item = TopHeader(
                imgUrl = "",
                name = "민지",
                dDay = "D+ 1",
                characterId = 1L,
            ),
            onBack = {}
        )
    }
}
package kr.co.call.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.chatting.ChatSummary

@Composable
fun FrontRow(
    modifier: Modifier = Modifier,
    isManager: Boolean = false,
    chatSummary: ChatSummary,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(102.dp)
            .background(CallTheme.colors.white.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // 프로필 이미지.
        // TODO: 추후 Coil을 통한 이미지 로드로 교체
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(CallTheme.colors.gray100),
            contentAlignment = Alignment.Center,
        ) {
            if (isManager) {
                Image(
                    painter = painterResource(R.drawable.ic_chat_manager),
                    contentDescription = "매니저",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = chatSummary.name.take(1),
                    style = CallTheme.typography.bodyMediumBold,
                    color = CallTheme.colors.gray600,
                )
            }
        }

        // 이름 + 최근 메시지
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chatSummary.name,
                    style = CallTheme.typography.bodyLargeBold,
                    color = if (isManager) CallTheme.colors.mainVariant1 else CallTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.size(8.dp))

                if (chatSummary.isMainCharacter) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chat_pin),
                        contentDescription = "메인 캐릭터",
                        tint = Color.Unspecified,
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = chatSummary.content,
                style = CallTheme.typography.bodySmall,
                color = CallTheme.colors.gray800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // 시간 + 읽지 않은 메시지 수
        if (!isManager) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = chatSummary.whenSubmitted,
                    style = CallTheme.typography.caption,
                    color = CallTheme.colors.gray800,
                )

                Spacer(Modifier.height(16.dp))

                if ((chatSummary.unReadMessageCount.toIntOrNull() ?: 0) > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    UnReadMessageCountBox(
                        unReadMessageCount = chatSummary.unReadMessageCount
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FrontRowPreview() {
    CallFromAiTheme {
        FrontRow(
            chatSummary = ChatSummary(
                image = "",
                name = "김민지",
                isMainCharacter = true,
                content = "오늘 저녁에 뭐해?",
                whenSubmitted = "30분 전",
                unReadMessageCount = "3",
                isAlarmEnabled = true,
            )
        )
    }
}

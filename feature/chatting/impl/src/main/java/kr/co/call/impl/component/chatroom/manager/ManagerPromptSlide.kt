package kr.co.call.impl.component.chatroom.manager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ManagerPromptSlide(
    onCallTimeClick: () -> Unit = {},
    onChangePartnerClick: () -> Unit = {},
    onEditRecordClick: () -> Unit = {},
    onInquiryClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(30.dp),
            )
            .background(Color.White, RoundedCornerShape(30.dp))
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    if (delta < -5f) isExpanded = true
                    if (delta > 5f) isExpanded = false
                }
            )
            .padding(top = 10.dp, bottom = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 드래그 핸들
        Box(
            modifier = Modifier
                .size(width = 62.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color(0xFFD9D2D4))
        )

        Spacer(modifier = Modifier.height(21.dp))

        // 기본 노출 항목
        PromptItem(emoji = "☎️", text = "전화는 언제 오나요?", onClick = onCallTimeClick)
        PromptItem(emoji = "👤", text = "상대방 정보를 변경하고 싶어요.", onClick = onChangePartnerClick)

        // 슬라이드 업 시 추가 노출 항목
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column {
                PromptItem(emoji = "📝", text = "기록한 정보를 수정하고 싶어요.", onClick = onEditRecordClick)
                PromptItem(emoji = "💬", text = "상담원에게 문의하기", onClick = onInquiryClick)
            }
        }
    }
}

@Composable
private fun PromptItem(
    emoji: String,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 23.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = CallTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            style = CallTheme.typography.bodyMedium.copy(
                color = CallTheme.colors.black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ManagerPromptSlidePreview() {
    CallFromAiTheme {
        ManagerPromptSlide()
    }
}

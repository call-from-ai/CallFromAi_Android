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
import kr.co.call.impl.intent.ManagerChatRoomIntent

/**
 * 매니저 채팅방에서 빠른 액션 프롬프트를 표시하는 드래그 가능한 슬라이드 업 패널 컴포저블입니다.
 *
 * 이 컴포넌트는 세로 방향 드래그 제스처를 통해 추가 옵션 영역을 확장하거나 축소할 수 있습니다.
 * 상단에 드래그 핸들을 포함하며, [AnimatedVisibility]를 사용하여
 * 접힌 상태와 펼쳐진 상태 사이의 전환 애니메이션을 처리합니다.
 *
 * @param onIntent 사용자 상호작용에 의해 발생한 [ManagerChatRoomIntent]를 전달하는 콜백 함수입니다.
 * @param modifier 이 컴포넌트의 최상위 컨테이너에 적용할 [Modifier]입니다.
 */
@Composable
fun ManagerPromptSlide(
    onIntent: (ManagerChatRoomIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 슬라이드 패널의 확장 상태 관리
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp),
            )
            .background(Color.White, RoundedCornerShape(30.dp))
            .draggable(
                orientation = Orientation.Vertical,
                // 세로 드래그 방향에 따라 패널 확장/축소 상태 변경
                // 위로 일정 거리 이상 드래그하면 확장,
                // 아래로 일정 거리 이상 드래그하면 축소
                state = rememberDraggableState { delta ->
                    if (delta < -5f) isExpanded = true
                    if (delta > 5f) isExpanded = false
                }
            )
            .padding(top = 10.dp, bottom = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 사용자에게 드래그 가능한 영역임을 나타내는 핸들 표시
        Box(
            modifier = Modifier
                .size(width = 62.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color(0xFFD9D2D4))
        )

        Spacer(modifier = Modifier.height(21.dp))

        // 기본 노출 항목
        PromptItem(emoji = "☎️", text = "전화는 언제 오나요?", onClick = { onIntent(ManagerChatRoomIntent.ClickWhenCall) })
        PromptItem(emoji = "👤", text = "상대방 정보를 변경하고 싶어요.", onClick = { onIntent(ManagerChatRoomIntent.ClickChangePartnerInfo) })

        // 슬라이드 업 시 추가 노출 항목
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column {
                PromptItem(emoji = "📝", text = "기록한 정보를 수정하고 싶어요.", onClick = { onIntent(ManagerChatRoomIntent.ClickUpdateInfo) })
                PromptItem(emoji = "💬", text = "상담원에게 문의하기", onClick = { onIntent(ManagerChatRoomIntent.ClickAskToAgent) })
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
            .clickable(onClick = onClick) // 액션 항목 전체 영역을 클릭 영역으로 사용
            .padding(horizontal = 23.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이모지 아이콘 표시
        Text(
            text = emoji,
            style = CallTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(14.dp))

        // 액션 설명 텍스트 표시
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
        ManagerPromptSlide(onIntent = {})
    }
}

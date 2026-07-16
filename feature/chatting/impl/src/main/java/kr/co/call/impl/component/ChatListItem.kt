package kr.co.call.impl.component

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.chatting.ChatSummary

@Composable
fun ChatListItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    onAlarmClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    chatSummary: ChatSummary
) {
    val state = remember { AnchoredDraggableState(initialValue = SwipeState.Closed) }

    SwipeableBox(
        swipeState = state,
        backgroundContent = {
            BackgroundRow(
                modifier = modifier,
                isAlarmEnabled = chatSummary.isAlarmEnabled,
                onAlarmClick = onAlarmClick,
                onDeleteClick = onDeleteClick
            )
        },
    ) {
        FrontRow(
            chatSummary = chatSummary,
            onClick = onItemClick 
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListItemPreview() {
    CallFromAiTheme {
        ChatListItem(
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

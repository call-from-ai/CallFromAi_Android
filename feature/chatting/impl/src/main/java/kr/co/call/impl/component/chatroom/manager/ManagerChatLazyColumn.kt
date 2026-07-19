package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.chatroom.ChatGrayBubble
import kr.co.call.impl.viewmodel.ManagerChatUiItem

@Composable
fun ManagerChatLazyColumn(
    chatItems: List<ManagerChatUiItem>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 13.dp
        )
    ) {
        items(
            items = chatItems,
            key = { it.message.id }
        ) { item ->
            when(val message = item.message) {

                is ManagerFirstMessage -> {
                    when (message.type) {

                        ManagerFirstMessageType.NORMAL -> {
                            ChatGrayBubble(
                                text = AnnotatedString(message.content),
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }

                        ManagerFirstMessageType.RELATIONSHIP -> {
                            ChatLightPinkBubbleWithIcon(
                                text = AnnotatedString(message.content),
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }
                    }
                }

            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun ManagerChatLazyColumnPreview() {
    val sampleChatItems = listOf(
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "1",
                content = "안녕하세요! 무엇을 도와드릴까요?",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:00"
        ),
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "2",
                content = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요.",
                type = ManagerFirstMessageType.RELATIONSHIP,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:01"
        ),
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "3",
                content = "로딩 중...",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = LocalDateTime.now()
            ),
            loadStatus = LoadStatus.Loading,
            time = "오후 2:02"
        )
    )

    CallFromAiTheme {
        ManagerChatLazyColumn(
            chatItems = sampleChatItems
        )
    }
}

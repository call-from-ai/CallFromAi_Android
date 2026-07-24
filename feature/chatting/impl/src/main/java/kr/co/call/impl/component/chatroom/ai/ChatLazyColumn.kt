package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.impl.component.chatroom.ChatGrayBubble
import kr.co.call.impl.component.chatroom.ChatPinkBubble
import kr.co.call.impl.component.chatroom.DateSeparator
import kr.co.call.impl.model.ChatItemUiModel

@Composable
fun ChatLazyColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    pagingItems: LazyPagingItems<ChatItemUiModel>,
    realtimeMessages: List<ChatItemUiModel> = emptyList(),
    deletedIds: Set<Long> = emptySet()
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 13.dp,
            bottom = 13.dp + bottomPadding,   // 오버레이 높이만큼 확보
        ),
    ) {
        // 페이징으로 불러오는 기존 메세지
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { item ->
                when (item) {
                    is ChatItemUiModel.Message -> item.chatMessageId
                    is ChatItemUiModel.DateSeparator -> "separator_${item.date}"
                    else -> {}
                }
            },
        ) { index ->
            when (val item = pagingItems[index]) {
                is ChatItemUiModel.Message -> {
                    if (item.chatMessageId !in deletedIds) {
                        when (item.senderType) {
                            SenderType.AI -> ChatGrayBubble(
                                text = AnnotatedString(item.content),
                                time = item.time,
                            )
                            SenderType.USER -> Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                ChatPinkBubble(
                                    text = item.content,
                                    time = item.time,
                                )
                            }
                            SenderType.UNKNOWN -> {}
                        }
                    }
                }
                is ChatItemUiModel.DateSeparator -> DateSeparator(
                    text = item.date
                )
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatLazyColumnPreview() {
    val dummyItems = listOf(
        ChatItemUiModel.DateSeparator(date = "2026년 7월 1일"),
        ChatItemUiModel.Message(
            chatMessageId = 1L,
            senderType = SenderType.AI,
            content = "오늘 하루 어땠어?",
            messageType = MessageType.TEXT,
            time = "13:00",
        ),
        ChatItemUiModel.Message(
            chatMessageId = 2L,
            senderType = SenderType.USER,
            content = "좋았어!",
            messageType = MessageType.TEXT,
            time = "13:01",
        ),
    )

    val pagingItems = flowOf(PagingData.from(dummyItems)).collectAsLazyPagingItems()

    CallFromAiTheme {
        ChatLazyColumn(pagingItems = pagingItems)
    }
}

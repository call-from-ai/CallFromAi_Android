package kr.co.call.impl.component.chatroom.ai


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.chatroom.DateSeparator
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.util.shouldShowDateSeparator

@Composable
fun ChatLazyColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
    pagingItems: LazyPagingItems<ChatItemUiModel>,
    realtimeMessages: List<ChatItemUiModel> = emptyList(),
    deletedIds: Set<Long> = emptySet(),
    selectedMessageId: Long? = null,
    onLongPress: (Long) -> Unit = {},
    onCopy: (String) -> Unit = {},
    onDelete: (Long) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val density = LocalDensity.current
    val popupOffsetY = with(density) { -88.dp.roundToPx() }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 2.dp,
            bottom = 13.dp + bottomPadding,
        ),
    ) {
        // SSE 로딩 버블(AI의 Loading 상태 메시지)을 앞으로 정렬해 reverseLayout 기준 항상 최하단에 표시
        // 삭제된 메시지는 슬롯 자체를 제거해 spacing이 남지 않도록 미리 필터링
        val visibleRealtimeMessages = buildList {
            addAll(realtimeMessages.filter {
                it is ChatItemUiModel.Message &&
                it.senderType == SenderType.AI &&
                it.loadStatus is LoadStatus.Loading
            })
            addAll(realtimeMessages.filter {
                it !is ChatItemUiModel.Message ||
                it.chatMessageId !in deletedIds &&
                !(it.senderType == SenderType.AI && it.loadStatus is LoadStatus.Loading)
            })
        }
        items(
            count = visibleRealtimeMessages.size,
            key = { index ->
                val item = visibleRealtimeMessages[index]
                if (item is ChatItemUiModel.Message) "rt_${item.clientId}" else "rt_$index"
            },
        ) { index ->
            val item = visibleRealtimeMessages[index]
            if (item is ChatItemUiModel.Message) {
                // Arrangement.spacedBy 대신 각 아이템에 직접 padding을 적용해
                // 삭제된 슬롯(0 높이)이 간격에 영향을 주는 문제를 방지
                Box(modifier = Modifier.padding(top = 11.dp)) {
                    ChatMessageRow(
                        item = item,
                        isSelected = item.chatMessageId == selectedMessageId,
                        onLongPress = onLongPress,
                        onCopy = onCopy,
                        onDelete = onDelete,
                        onDismiss = onDismiss,
                        popupOffsetY = popupOffsetY,
                    )
                }
            }
        }

        // 페이징으로 불러오는 기존 메시지 (ViewModel에서 삭제 필터링 완료)
        // pagingItems[index] 접근으로 Paging 라이브러리에 로드 트리거를 전달
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { item ->
                when (item) {
                    is ChatItemUiModel.Message -> item.chatMessageId
                    is ChatItemUiModel.DateSeparator -> "separator_${item.date}"
                    else -> item.hashCode()
                }
            },
        ) { index ->
            val item = pagingItems[index] ?: return@items

            when (item) {
                is ChatItemUiModel.Message -> {
                    if (item.chatMessageId !in deletedIds) {
                        Box(modifier = Modifier.padding(top = 11.dp)) {
                            ChatMessageRow(
                                item = item,
                                isSelected = item.chatMessageId == selectedMessageId,
                                onLongPress = onLongPress,
                                onCopy = onCopy,
                                onDelete = onDelete,
                                onDismiss = onDismiss,
                                popupOffsetY = popupOffsetY,
                            )
                        }
                    }
                    // 삭제된 메시지: 빈 블록 → 슬롯 크기 0, padding 없음 → 간격 영향 없음
                }

                is ChatItemUiModel.DateSeparator -> {
                    if (shouldShowDateSeparator(pagingItems, index, deletedIds)) {
                        DateSeparator(
                            text = item.date,
                            modifier = Modifier.padding(top = 11.dp, bottom = 10.dp)
                        )
                    }
                }

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

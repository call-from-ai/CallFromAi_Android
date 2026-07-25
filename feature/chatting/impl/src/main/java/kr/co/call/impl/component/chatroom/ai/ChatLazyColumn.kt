package kr.co.call.impl.component.chatroom.ai


import androidx.compose.foundation.layout.Arrangement
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
import kr.co.call.impl.component.chatroom.DateSeparator
import kr.co.call.impl.model.ChatItemUiModel

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
        verticalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 13.dp,
            bottom = 13.dp + bottomPadding,
        ),
    ) {
        // 낙관적(실시간) 메시지: reverseLayout=true 이므로 맨 앞 = 화면 하단
        // 삭제된 메시지는 슬롯 자체를 제거해 spacing이 남지 않도록 미리 필터링
        val visibleRealtimeMessages = realtimeMessages.filter {
            it !is ChatItemUiModel.Message || it.chatMessageId !in deletedIds
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

        // 삭제된 메시지를 제외한 실제 표시 대상 페이징 아이템
        // LazyColumn 슬롯 자체를 제거하기 위해 렌더링 전에 필터링 처리
        val visiblePagingItems = pagingItems.itemSnapshotList.items.filter {
            it !is ChatItemUiModel.Message || it.chatMessageId !in deletedIds
        }

        // 페이징으로 불러오는 기존 메시지
        // 삭제된 메시지는 items count에서 제외되어 빈 공간이 남지 않도록 처리
        items(
            count = visiblePagingItems.size,
            key = { index ->
                when (val item = visiblePagingItems[index]) {
                    is ChatItemUiModel.Message -> item.chatMessageId
                    is ChatItemUiModel.DateSeparator -> "separator_${item.date}"
                    else -> index
                }
            },
        ) { index ->
            when (val item = visiblePagingItems[index]) {

                is ChatItemUiModel.Message -> {
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

                is ChatItemUiModel.DateSeparator -> {
                    // 날짜 구분선 아래에 표시 가능한 메시지가 존재하는지 확인
                    // 해당 날짜의 모든 메시지가 삭제된 경우 날짜 구분선도 숨김 처리
                    var hasVisibleMessage = false

                    // 현재 날짜 구분선 이전 메시지를 역순으로 탐색
                    // 같은 날짜 범위 안에 표시 가능한 메시지가 있는지 확인
                    for (i in (index - 1) downTo 0) {
                        when (val prev = visiblePagingItems[i]) {

                            // 이전 날짜 구분선을 만나면 현재 날짜 범위 탐색 종료
                            is ChatItemUiModel.DateSeparator -> break

                            // 삭제되지 않은 메시지가 존재하면 날짜 구분선 표시
                            is ChatItemUiModel.Message -> {
                                hasVisibleMessage = true
                                break
                            }

                            else -> {}
                        }
                    }

                    // 표시 가능한 메시지가 존재하는 날짜만 구분선 렌더링
                    if (hasVisibleMessage) {
                        DateSeparator(
                            text = item.date,
                            modifier = Modifier.padding(vertical = 5.dp)
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

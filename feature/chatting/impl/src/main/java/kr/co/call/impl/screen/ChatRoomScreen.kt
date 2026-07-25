package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.chatroom.ai.ChatLazyColumn
import kr.co.call.impl.component.chatroom.ai.ChatTextField
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.TopHeader
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.impl.component.chatroom.ai.ChatTopBar
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import kr.co.call.impl.viewmodel.ChatRoomViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    onBack: () -> Unit = {},
) {
    val state = viewModel.collectAsState().value
    val pagingItems: LazyPagingItems<ChatItemUiModel> = viewModel.chats.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChatRoomSideEffect.ShowToast -> {}
            is ChatRoomSideEffect.Call -> {}
        }
    }

    ChatRoomScreenContent(
        modifier = modifier,
        state = state,
        pagingItems = pagingItems,
        listState = listState,
        onBack = onBack,
        onCallClick = {},
        onValueChange = {},
        onSendClick = {},
        onCameraClick = {},
    )
}

@Composable
fun ChatRoomScreenContent(
    modifier: Modifier = Modifier,
    state: ChatRoomUiState = ChatRoomUiState(),
    pagingItems: LazyPagingItems<ChatItemUiModel>,
    listState: LazyListState = rememberLazyListState(),
    onBack: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
) {
    val density = LocalDensity.current
    var overlayHeight by remember { mutableStateOf(0.dp) }

    val imeBottom = WindowInsets.ime.getBottom(density)

    // 키보드 올라올 때 최신 메시지로 자연스럽게 따라 스크롤
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
    ) {
        // 탑바: 고정
        ChatTopBar(
            modifier = Modifier.fillMaxWidth(),
            item = state.topHeader,
            onBack = onBack,
            onCallClick = onCallClick,
        )

        // 리스트 + 텍스트필드 겹침 영역. 이 영역만 키보드 따라 올라감
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            // 리스트: Box 전체를 채움 (텍스트필드 뒤까지 깔림)
            ChatLazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                pagingItems = pagingItems,
                realtimeMessages = state.chatItems,
                listState = listState,
                bottomPadding = overlayHeight,   // 마지막 메시지가 텍스트필드에 안 가리게
                deletedIds = state.deletedIds,
            )

            // 텍스트필드: 리스트 위에 겹쳐서 바닥에 고정
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .onSizeChanged {
                        overlayHeight = with(density) { it.height.toDp() }
                    }
            ) {
                ChatTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    state = state.textFieldState,
                    onValueChange = onValueChange,
                    onCameraClick = onCameraClick,
                    onSendClick = onSendClick,
                )

                Spacer(Modifier.height(15.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatRoomScreenContentPreview() {
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
        ChatItemUiModel.Message(
            chatMessageId = 3L,
            senderType = SenderType.AI,
            content = "그랬구나, 오늘도 수고했어.",
            messageType = MessageType.TEXT,
            time = "13:02",
        ),
    )
    val pagingItems = flowOf(PagingData.from(dummyItems)).collectAsLazyPagingItems()

    CallFromAiTheme {
        ChatRoomScreenContent(
            state = ChatRoomUiState(
                topHeader = TopHeader(
                    name = "AI 친구",
                    dDay = "D+ 12"
                )
            ),
            pagingItems = pagingItems,
        )
    }
}

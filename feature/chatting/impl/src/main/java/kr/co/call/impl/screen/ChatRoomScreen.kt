package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
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
    roomId: Long,
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel = hiltViewModel(),
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(8.dp))

            ChatTopBar(
                modifier = Modifier.fillMaxWidth(),
                item = state.topHeader,
                onBack = onBack,
                onCallClick = onCallClick,
            )

            ChatLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CallTheme.colors.background),
                pagingItems = pagingItems,
                realtimeMessages = state.chatItems,
                listState = listState,
                bottomPadding = overlayHeight,
                deletedIds = state.deletedIds,
            )
        }

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

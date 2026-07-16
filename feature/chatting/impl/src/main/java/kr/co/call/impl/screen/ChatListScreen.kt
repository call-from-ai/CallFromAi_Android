package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.impl.component.ChatListItem
import kr.co.call.impl.component.FrontRow
import kr.co.call.impl.model.ChatListState
import kr.co.call.impl.viewmodel.ChatListViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    ChatListScreenContent(
        state = state,
        modifier = modifier
    )
}

@Composable
fun ChatListScreenContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "채팅",
            style = CallTheme.typography.titleMediumBold,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                count = state.chatList.size,
                key = { index -> state.chatList[index].chatRoomId }
            ) {
                ChatListItem(
                    chatSummary = state.chatList[it]
                )
            }

            // 매니저는 맨 아래 고정
            item {
                FrontRow(
                    isManager = true,
                    chatSummary = ChatSummary(
                        image = "",
                        name = "전화왔어 매니저",
                        isMainCharacter = true,
                        content = "오늘 저녁에 뭐해?",
                        whenSubmitted = "30분 전",
                        unReadMessageCount = "3",
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListScreenPreview() {
    CallFromAiTheme {
        ChatListScreenContent(
            state = ChatListState(
                chatList = listOf(
                    ChatSummary(
                        chatRoomId = 1,
                        image = "",
                        name = "김철수",
                        isMainCharacter = false,
                        content = "안녕하세요! 오늘 날씨가 참 좋네요.",
                        whenSubmitted = "방금 전",
                        unReadMessageCount = "1"
                    ),
                    ChatSummary(
                        chatRoomId = 2,
                        image = "",
                        name = "이영희",
                        isMainCharacter = true,
                        content = "오늘 뭐해? 같이 영화 볼래?",
                        whenSubmitted = "30분 전",
                        unReadMessageCount = "5"
                    )
                )
            )
        )
    }
}


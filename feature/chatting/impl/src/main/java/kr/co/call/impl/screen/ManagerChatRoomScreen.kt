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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import kr.co.call.impl.component.chatroom.manager.ManagerChatField
import kr.co.call.impl.component.chatroom.manager.ManagerChatLazyColumn
import kr.co.call.impl.component.chatroom.manager.ManagerChatTopBar
import kr.co.call.impl.component.chatroom.manager.ManagerPromptSlide
import kr.co.call.impl.intent.ManagerChatRoomIntent
import kr.co.call.impl.state.ManagerChatRoomUiState
import kr.co.call.impl.viewmodel.ManagerChatRoomViewModel
import kr.co.call.impl.model.ManagerChatUiItem
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ManagerChatRoomScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ManagerChatRoomViewModel = hiltViewModel()
) {
    // 상태 구독
    val state = viewModel.collectAsState().value

    ManagerChatRoomScreenContent(
        modifier = modifier,
        onBack = onBack,
        state = state, // 상태 호이스팅
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun ManagerChatRoomScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    state: ManagerChatRoomUiState,
    onIntent: (ManagerChatRoomIntent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(8.dp))

            ManagerChatTopBar(
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )

            ManagerChatLazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CallTheme.colors.background),
                chatItems = state.chatItems
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ManagerPromptSlide(
                onIntent = onIntent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(15.dp))

            ManagerChatField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(15.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManagerChatRoomScreenContentPreview() {
    CallFromAiTheme {
        ManagerChatRoomScreenContent(
            state = ManagerChatRoomUiState(
                chatItems = listOf(
                    ManagerChatUiItem(
                        message = ManagerFirstMessage(
                            id = "1",
                            content = "안녕하세요! 무엇을 도와드릴까요?",
                            type = ManagerFirstMessageType.NORMAL,
                            createdAt = LocalDateTime.now()
                        ),
                        time = "오전 10:00"
                    )
                )
            ),
            onBack = {},
            onIntent = {}
        )
    }
}



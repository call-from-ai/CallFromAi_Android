package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import kr.co.call.impl.component.chatroom.manager.ManagerChatLazyColumn
import kr.co.call.impl.component.chatroom.manager.ManagerChatTopBar
import kr.co.call.impl.viewmodel.ManagerChatRoomUiState
import kr.co.call.impl.viewmodel.ManagerChatRoomViewModel
import kr.co.call.impl.viewmodel.ManagerChatUiItem
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
        state = state // 상태 호이스팅
    )
}

@Composable
fun ManagerChatRoomScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    state: ManagerChatRoomUiState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(8.dp))

        ManagerChatTopBar(
            modifier = Modifier.fillMaxWidth(),
            onBack = onBack
        )

        ManagerChatLazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(CallTheme.colors.background),
            chatItems = state.chatItems
        )
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
            onBack = {}
        )
    }
}



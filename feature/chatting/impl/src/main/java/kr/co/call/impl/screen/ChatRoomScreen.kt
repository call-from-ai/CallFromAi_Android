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
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.chatroom.ai.ChatTextField
import kr.co.call.impl.component.chatroom.ai.ChatTopBar
import kr.co.call.impl.model.TextFieldState
import kr.co.call.impl.viewmodel.ChatRoomViewModel

@Composable
fun ChatRoomScreen(
    roomId: Long,
    modifier: Modifier = Modifier,
    //viewModel: ChatRoomViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    ChatRoomScreenContent(
        modifier = modifier,
        onBack = onBack
    )
}

@Composable
fun ChatRoomScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    textFieldState: TextFieldState = TextFieldState(),
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
                onBack = onBack
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
                state = textFieldState,
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
    CallFromAiTheme {
        ChatRoomScreenContent(
            onBack = {}
        )
    }
}

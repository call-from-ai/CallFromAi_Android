package kr.co.call.impl.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.impl.component.chatroom.manager.ManagerChatTopBar

@Composable
fun ManagerChatRoomScreen(
    modifier: Modifier = Modifier,
) {
    
    ManagerChatRoomScreenContent(
        modifier = modifier
    )
}

@Composable
fun ManagerChatRoomScreenContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.height(8.dp))

        ManagerChatTopBar(modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun ManagerChatRoomScreenPreview() {
    CallFromAiTheme {
        ManagerChatRoomScreen()
    }
}


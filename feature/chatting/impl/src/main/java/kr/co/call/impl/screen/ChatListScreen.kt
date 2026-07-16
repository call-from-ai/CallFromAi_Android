package kr.co.call.impl.screen

import androidx.compose.foundation.background
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
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
) {
    ChatListScreenContent(
        modifier = modifier
    )
}

@Composable
fun ChatListScreenContent(
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
            modifier = Modifier
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListScreenPreview() {
    CallFromAiTheme {
        ChatListScreenContent()
    }
}

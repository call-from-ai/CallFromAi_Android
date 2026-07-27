package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar

@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding(),
    ) {
        CommonTopAppBar(
            title = "구독",
            onBackClick = onBackClick,
        )
        // TODO: 구독 유형 선택 UI
    }
}

@Preview(showBackground = true)
@Composable
private fun SubscriptionScreenPreview() {
    CallFromAiTheme {
        SubscriptionScreen(onBackClick = {})
    }
}

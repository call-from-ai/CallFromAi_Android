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
fun EditProfileScreen(
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
            title = "프로필 수정",
            onBackClick = onBackClick,
        )
        // TODO: 내 정보 수정 UI
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    CallFromAiTheme {
        EditProfileScreen(onBackClick = {})
    }
}

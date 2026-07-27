package kr.co.call.impl.component.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 통화 기록을 불러오는 동안 표시하는 로딩 영역
 */
@Composable
fun CallRecordLoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = CallTheme.colors.mainVariant1,
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 600)
@Composable
private fun CallRecordLoadingContentPreview() {
    CallFromAiTheme {
        CallRecordLoadingContent()
    }
}

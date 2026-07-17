package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 읽지 않은 상태를 표시하는 컴포넌트
 * - 홈 헤더와 알림 목록에서 공통 사용
 */
@Composable
internal fun UnreadIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(6.dp)
            .background(
                color = CallTheme.colors.subRed,
                shape = CircleShape,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun UnreadIndicatorPreview() {
    CallFromAiTheme {
        UnreadIndicator()
    }
}

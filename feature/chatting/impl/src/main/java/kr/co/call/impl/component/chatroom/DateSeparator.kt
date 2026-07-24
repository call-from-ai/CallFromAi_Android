package kr.co.call.impl.component.chatroom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun DateSeparator(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.gray400
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateSeparatorPreview() {
    CallFromAiTheme {
        DateSeparator(
            text = "2026년 5월 31일"
        )
    }
}
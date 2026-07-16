package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun UnReadMessageCountBox(
    unReadMessageCount: String = "0"
) {
    val shape = RoundedCornerShape(3.dp)

    Box(
        modifier = Modifier
            .size(20.dp)
            .background(color = CallTheme.colors.subRed, shape = shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = unReadMessageCount,
            style = CallTheme.typography.bodySmallBold,
            color = CallTheme.colors.white,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnReadMessageCountBoxPreview() {
    CallFromAiTheme {
        UnReadMessageCountBox(
            unReadMessageCount = "12"
        )
    }
}
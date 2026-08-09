package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun CallHeader(
    isIncoming: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "전화왔어",
            color = CallTheme.colors.mainVariant1,
            style = CallTheme.typography.bodyLargeBold,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(CallTheme.colors.mainVariant1),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (isIncoming) "수신전화" else "발신전화",
            color = CallTheme.colors.gray400,
            style = CallTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CallHeaderPreview() {
    CallFromAiTheme {
        Column {
            CallHeader(isIncoming = true)
            Spacer(modifier = Modifier.height(12.dp))
            CallHeader(isIncoming = false)
        }
    }
}

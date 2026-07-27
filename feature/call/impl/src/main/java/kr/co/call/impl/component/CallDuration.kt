package kr.co.call.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun CallDuration(
    durationSeconds: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_call_phone_small),
            contentDescription = null,
            tint = CallTheme.colors.gray900,
        )
        Text(
            text = durationSeconds.toCallDuration(),
            color = CallTheme.colors.gray900,
            style = CallTheme.typography.bodyMedium,
        )
    }
}

// 통화 시간을 분:초 형식으로 변환
private fun Int.toCallDuration(): String {
    val safeSeconds = coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val seconds = safeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun CallDurationPreview() {
    CallFromAiTheme {
        CallDuration(durationSeconds = 152)
    }
}

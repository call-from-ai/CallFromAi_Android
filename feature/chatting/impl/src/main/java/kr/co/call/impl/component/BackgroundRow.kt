package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun BackgroundRow(
    modifier: Modifier = Modifier,
    isAlarmEnabled: Boolean = true,
    onAlarmClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .size(204.dp, 102.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
                .fillMaxHeight()
                .background(CallTheme.colors.mainVariant3)
                .clickable { onAlarmClick() },
            contentAlignment = Alignment.Center
        ) {

            val iconPainter = if (isAlarmEnabled)
                painterResource(R.drawable.ic_chat_bell)
            else painterResource(R.drawable.ic_chat_bell_off)


            Icon(
                painter = iconPainter,
                tint = Color.Unspecified,
                contentDescription = "알람 설정 버튼"
            )
        }

        Box(
            modifier = Modifier.weight(1f)
                .fillMaxHeight()
                .background(CallTheme.colors.gray400)
                .clickable { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "목록에서\n지우기",
                textAlign = TextAlign.Center,
                style = CallTheme.typography.bodySmallBold,
                color = CallTheme.colors.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BackgroundRowPreview() {
    CallFromAiTheme {
        BackgroundRow(isAlarmEnabled = true)
    }
}

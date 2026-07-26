package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 버튼 컴포넌트 (마이페이지>ProfileCard의 충전/구매/내역)
 * */
@Composable
fun TicketActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .width(105.dp)
            .background(CallTheme.colors.gray100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = CallTheme.typography.bodyMediumMedium,
            color = CallTheme.colors.black,
        )
    }
}

@Preview
@Composable
private fun SettingsSectionCardPreview2() {
    CallFromAiTheme {
        TicketActionButton(label = "충전", onClick = { })
    }
}

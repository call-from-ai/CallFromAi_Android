package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 티켓 정보 요약 : 통화 이용권 + 충전/구매/내역 버튼
 */
@Composable
fun ProfileTicketContent(
    remainingCount: Int,
    onChargeClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 통화 이용권
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mypage_ticket),
                contentDescription = null,
                tint = CallTheme.colors.mainVariant1,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "통화 이용권",
                style = CallTheme.typography.bodySmallBold,
                color = CallTheme.colors.mainVariant1,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${remainingCount}회 남음",
                style = CallTheme.typography.bodySmallBold,
                color = CallTheme.colors.gray900,
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 충전/구매/내역 버튼
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            TicketActionButton(label = "충전", onClick = onChargeClick, modifier = Modifier.weight(1f))
            TicketActionButton(label = "구매", onClick = onPurchaseClick, modifier = Modifier.weight(1f))
            TicketActionButton(label = "내역", onClick = onHistoryClick, modifier = Modifier.weight(1f))
        }
    }
}

// 버튼 컴포넌트
@Composable
private fun TicketActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
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

@Preview(showBackground = true)
@Composable
private fun CallTicketCardPreview() {
    CallFromAiTheme {
        ProfileTicketContent(
            remainingCount = 18,
            onChargeClick = {},
            onPurchaseClick = {},
            onHistoryClick = {},
        )
    }
}
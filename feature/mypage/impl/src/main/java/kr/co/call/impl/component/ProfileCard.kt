package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 상단 프로필 카드 : 프로필 정보 요약 + 티켓 정보 요약
 */
@Composable
fun ProfileCard(
    profileImageUrl: String,
    nickname: String,
    tier: String,
    remainingTicketCount: Int,
    onProfileClick: () -> Unit,
    onChargeClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CallTheme.colors.white)
            .border(width = 1.dp, color = CallTheme.colors.gray100, shape =cardShape ),
    ) {
        // 프로필 정보 요약
        ProfileSummaryContent(
            profileImageUrl = profileImageUrl,
            nickname = nickname,
            tier = tier,
            onClick = onProfileClick,
        )

        HorizontalDivider(color = CallTheme.colors.gray100)

        // 티켓 정보 요약
        ProfileTicketContent(
            remainingCount = remainingTicketCount,
            onChargeClick = onChargeClick,
            onPurchaseClick = onPurchaseClick,
            onHistoryClick = onHistoryClick,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 15.dp, bottom = 28.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTicketCardPreview(){
    CallFromAiTheme {
        ProfileCard(
            profileImageUrl = "",
            nickname = "김수현",
            tier = "Basic",
            remainingTicketCount = 18,
            onProfileClick={},
            onChargeClick = {},
            onPurchaseClick = {},
            onHistoryClick = {},
        )
    }
}
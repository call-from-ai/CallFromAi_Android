package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.model.PlanBadgeType

/**
* 구독 상품 카드용(SubscriptionPlanCard) 뱃지  : 추천상품, 인기상품
* */
@Composable
fun PlanBadgeChip(
    type: PlanBadgeType,
    modifier: Modifier = Modifier,
) {
    val (label, background, contentColor) = when (type) {
        PlanBadgeType.RECOMMENDED -> Triple(
            "추천 상품",
            CallTheme.colors.mainVariant2,
            CallTheme.colors.mainVariant1,
        )
        PlanBadgeType.POPULAR -> Triple(
            "인기 상품",
            CallTheme.colors.subRed,
            CallTheme.colors.white,
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = CallTheme.typography.captionSmallBold,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanBadgeChipPreview() {
    CallFromAiTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            PlanBadgeChip(type = PlanBadgeType.RECOMMENDED)
            PlanBadgeChip(type = PlanBadgeType.POPULAR)
        }
    }
}

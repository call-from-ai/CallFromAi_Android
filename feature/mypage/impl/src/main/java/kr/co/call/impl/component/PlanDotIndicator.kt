package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
* 구독 상품 카드용(SubscriptionPlanCard) DotIndicator
* */
@Composable
fun PlanDotIndicator(
    filledCount: Int,
    totalCount: Int = 3,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < filledCount) {
                            CallTheme.colors.mainVariant1
                        } else {
                            CallTheme.colors.white
                        },
                    )
                    .border(
                        width = 0.5.dp,
                        color = CallTheme.colors.mainVariant1,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanDotIndicatorPreview() {
    CallFromAiTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PlanDotIndicator(filledCount = 1)
            PlanDotIndicator(filledCount = 2)
            PlanDotIndicator(filledCount = 3)
        }
    }
}

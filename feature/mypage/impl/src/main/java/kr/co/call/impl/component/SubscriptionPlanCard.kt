package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.model.PlanFeatureUiModel
import kr.co.call.impl.model.SubscriptionPlanMock
import kr.co.call.impl.model.SubscriptionPlanUiModel

/**
 * 구독 상세 페이지(SubscriptionScreen)의 구독 상세 카드 (플랜이름+가격+설명(통화/관계/캐릭터 별)
 * */
@Composable
fun SubscriptionPlanCard(
    plan: SubscriptionPlanUiModel,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(15.dp)
    val borderColor = if (isSelected) {
        CallTheme.colors.mainVariant1
    } else {
        CallTheme.colors.gray200
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = if(isSelected) 1.5.dp else 1.dp, color = borderColor, shape = shape)
            .background(CallTheme.colors.white)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 플랜 코드(FREE/BASIC/PREMIUM)
                    Text(
                        text = plan.code,
                        style = CallTheme.typography.captionBold.copy(
                            brush = CallTheme.colors.chatGradient, // TextStyle.brush
                        ),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // 플랜별 DotIndicator
                    PlanDotIndicator(filledCount = plan.dotCount)
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 플랜 이름 (무료/베이직/프리미엄)
                    Text(
                        text = plan.name,
                        style = CallTheme.typography.bodyMediumBold,
                        color = CallTheme.colors.black,
                    )
                    // 플랜 뱃지(추천 상품/인기 상품)
                    if (plan.badge != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        PlanBadgeChip(type = plan.badge)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (isCurrent) {
                    Text(
                        text = "현재 이용중",
                        style = CallTheme.typography.captionBold,
                        color = CallTheme.colors.gray400,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                // 플랜 가격
                Text(
                    text = plan.priceLabel,
                    style = if (isCurrent) {
                        CallTheme.typography.bodyMediumBold.copy(
                            brush = CallTheme.colors.chatGradient
                        )
                    } else {
                        CallTheme.typography.bodyMediumBold.copy(
                            color = CallTheme.colors.gray900
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        // 플랜 설명
        Text(
            text = plan.description,
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.gray800,
        )

        Spacer(modifier = Modifier.height(14.dp))

        HorizontalDivider(color = CallTheme.colors.gray100)

        Spacer(modifier = Modifier.height(14.dp))

        // 플랜 설명 상세 (통화/관계/캐릭터)
        plan.features.forEachIndexed { index, feature ->
            if (index > 0) Spacer(modifier = Modifier.height(5.dp))
            PlanFeatureRow(feature = feature)
        }
    }
}

// 플랜 설명 상세 행(통화/관계/캐릭터)
@Composable
fun PlanFeatureRow(
    feature: PlanFeatureUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        // 설명 라벨(통화/관계/캐릭터)
        Text(
            text = feature.label,
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.gray600,
        )
        // 라벨에 따른 상세 설명
        Text(
            text = feature.value,
            style = CallTheme.typography.bodySmall,
            color = CallTheme.colors.black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubscriptionPlanCardPreview() {
    CallFromAiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SubscriptionPlanMock.plans.forEachIndexed { index, plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    isSelected = index == 1,
                    isCurrent = index == 1,
                    onClick = {},
                )
            }
        }
    }
}

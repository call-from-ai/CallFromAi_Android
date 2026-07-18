package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

// 관계 요약
/**
 * 관계 정보를 요약하는 컴포넌트
 * - 관계 일수와 통화 횟수 표시
 * - 연속 통화 일수 표시
 */
@Composable
fun RelationshipSummaryCard(
    summary: HomeSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .background(
                color = CallTheme.colors.white,
                shape = cardShape,
            )
            .border(
                width = 1.dp,
                color = CallTheme.colors.gray100,
                shape = cardShape,
            )
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RelationshipSummaryItem(
            label = "관계",
            value = summary.relationshipDaysText,
            modifier = Modifier.weight(1f),
        )

        RelationshipSummaryDivider()

        RelationshipSummaryItem(
            label = "통화",
            value = summary.totalCallCountText,
            modifier = Modifier.weight(1f),
        )

        RelationshipSummaryDivider()

        RelationshipSummaryItem(
            label = "연속 통화",
            value = summary.callStreakDaysText,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * 관계 요약 항목 컴포넌트
 * - 항목명과 값을 세로로 표시
 */
@Composable
private fun RelationshipSummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = CallTheme.colors.black,
            style = CallTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = CallTheme.colors.mainVariant1,
            style = CallTheme.typography.bodyMediumMedium,
        )
    }
}

/**
 * 관계 요약 구분선 컴포넌트
 * - 항목 사이에 세로 구분선 표시
 */
@Composable
private fun RelationshipSummaryDivider() {
    VerticalDivider(
        modifier = Modifier.height(63.dp),
        thickness = 1.dp,
        color = CallTheme.colors.gray100,
    )
}

/**
 * 관계 요약 카드 Preview
 * - 관계와 통화 샘플 데이터 표시
 */
@Preview(showBackground = true, widthDp = 350)
@Composable
private fun RelationshipSummaryCardPreview() {
    CallFromAiTheme {
        RelationshipSummaryCard(
            summary = HomeSummaryUiModel(
                relationshipDaysText = "30일째",
                totalCallCountText = "24회",
                callStreakDaysText = "12일",
            ),
            modifier = Modifier.height(94.dp),
        )
    }
}

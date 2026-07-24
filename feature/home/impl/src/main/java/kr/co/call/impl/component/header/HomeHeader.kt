package kr.co.call.impl.component.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.RelationshipSummaryCard
import kr.co.call.impl.component.UnreadIndicator
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

/**
 * 홈 화면 상단 헤더 컴포넌트
 * - 사용자 인사말과 알림 상태 표시
 * - 전화 버튼과 관계 요약 카드 포함
 */
@Composable
fun HomeHeader(
    summary: HomeSummaryUiModel,
    hasUnreadNotification: Boolean,
    onCharacterChangeClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(312.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(265.dp)
                .background(CallTheme.colors.mainVariant3),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 7.dp,
                    end = 3.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 메인 연인 교체하기 버튼
            Button(
                onClick = onCharacterChangeClick,
                modifier = Modifier.size(
                    width = 93.dp,
                    height = 24.dp,
                ),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CallTheme.colors.background.copy(alpha = 0.6f),
                    contentColor = CallTheme.colors.gray800,
                ),
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 3.dp,
                ),
            ) {
                Text(
                    text = "메인 연인 교체하기",
                    style = CallTheme.typography.caption.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(48.dp),
            ) {
                Box(modifier = Modifier.size(24.dp)) {
                    // 알림 아이콘
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_home_alarm,
                        ),
                        contentDescription = "알림",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(
                                width = 19.dp,
                                height = 21.dp,
                            ),
                        tint = Color.Unspecified,
                    )

                    // 알림 읽음 여부 표시
                    if (hasUnreadNotification) {
                        UnreadIndicator(
                            modifier = Modifier
                                .align(Alignment.TopEnd),
                        )
                    }
                }
            }
        }

        // 헤더 텍스트 영역
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 80.dp,
                end = 16.dp,
            ),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.SemiBold),
                    ) {
                        append(summary.firstName)
                    }
                    append("님, 반가워요! 👋🏻")
                },
                color = CallTheme.colors.white,
                style = CallTheme.typography.titleSmall,
            )

            Text(
                text = "오늘은 어떤 이야기를\n나누게 될까요?",
                color = CallTheme.colors.white,
                style = CallTheme.typography.titleMediumBold,
            )
        }

        // 전화하기 버튼
        FilledIconButton(
            onClick = onCallClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 133.dp,
                    end = 19.dp,
                )
                .size(53.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = CallTheme.colors.background,
                contentColor = CallTheme.colors.mainVariant1,
            ),
        ) {
            // 전화하기 버튼
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_home_call,
                ),
                contentDescription = "전화하기",
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified,
            )
        }

        // 관계 요약
        RelationshipSummaryCard(
            summary = summary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 217.dp)
                .width(350.dp)
                .height(94.dp),
        )


    }
}

/**
 * 홈 헤더 Preview
 * - 사용자와 관계 요약 샘플 데이터 표시
 * - 읽지 않은 알림 상태 확인
 */
@Preview(showBackground = true, widthDp = 412)
@Composable
private fun HomeHeaderPreview() {
    CallFromAiTheme {
        HomeHeader(
            summary = HomeSummaryUiModel(
                firstName = "수현",
                relationshipDaysText = "30일째",
                totalCallCountText = "24회",
                callStreakDaysText = "12일",
            ),
            hasUnreadNotification = true,
            onCharacterChangeClick = {},
            onNotificationClick = {},
            onCallClick = {},
        )
    }
}

package kr.co.call.impl.component.header

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.component.ProfileImageWithIcon
import kr.co.call.impl.viewmodel.model.HomeReservationUiModel

/**
 * 홈 화면의 통화 약속 컴포넌트
 * - 날짜와 전체 약속 건수는 항상 표시
 * - 약속이 있으면 상대 정보와 시간 변경 버튼 표시
 */
@Composable
internal fun HomeReservationCard(
    reservation: HomeReservationUiModel,
    modifier: Modifier = Modifier,
    onTimeChangeClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(CallTheme.colors.background),
    ) {
        // 날짜 항상 표시
        ReservationDate(
            reservationCountText = reservation.reservationCountText,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 35.dp,
                    top = 23.dp,
                ),
        )

        // 예약이 있을 때만 카드 상세 표시
        if (reservation.hasReservation) {
            ReservationContent(
                reservation = reservation,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 34.dp,
                        top = 61.dp,
                    ),
            )

            reservation.scheduledTimeText?.let { scheduledTimeText ->
                ReservationTimeActions(
                    scheduledTimeText = scheduledTimeText,
                    onTimeChangeClick = onTimeChangeClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = 56.dp,
                            end = 24.dp,
                        ),
                )
            }
        }
    }
}

/**
 * 약속 날짜 영역 컴포넌트
 * - 오늘 날짜와 전체 약속 건수 표시
 */
@Composable
private fun ReservationDate(
    reservationCountText: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = TimeUtil.toHomeDateText(LocalDate.now()),
            color = CallTheme.colors.gray900,
            style = CallTheme.typography.bodySmallBold,
        )

        Spacer(modifier = Modifier.width(10.dp))

        VerticalDivider(
            modifier = Modifier.height(21.dp),
            thickness = 2.dp,
            color = CallTheme.colors.gray100,
        )

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = reservationCountText,
            color = CallTheme.colors.mainVariant1,
            style = CallTheme.typography.bodySmallBold,
        )
    }
}

/**
 * 약속 상대 정보 컴포넌트
 * - 프로필 이미지와 통화 로고 표시
 * - 이름과 시간이 모두 있을 때 안내 문구 표시
 */
@Composable
private fun ReservationContent(
    reservation: HomeReservationUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImageWithIcon(
            profileImageUrl = reservation.profileImageUrl,
            showCallBadge = true,
        )

        Spacer(modifier = Modifier.width(16.dp))

        val firstName = reservation.firstName
        val scheduledAtText = reservation.scheduledAtText

        if (firstName != null && scheduledAtText != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(CallTheme.typography.bodyMediumMedium.toSpanStyle()) {
                        append(firstName)
                    }
                    append("님과 $scheduledAtText\n")
                    append("전화가 예정되어 있어요")
                },
                color = CallTheme.colors.black,
                style = CallTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * 약속 시간 변경 영역 컴포넌트
 * - 예약 시간을 24시간 형식으로 표시
 * - 시간 변경 이벤트 전달
 */
@Composable
private fun ReservationTimeActions(
    scheduledTimeText: String,
    onTimeChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(85.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(27.dp)
                .background(
                    color = CallTheme.colors.mainVariant2,
                    shape = RoundedCornerShape(50.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = scheduledTimeText,
                color = CallTheme.colors.mainVariant1,
                style = CallTheme.typography.bodySmallBold,
            )
        }

        // 시간 변경 버튼
        Button(
            onClick = onTimeChangeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(27.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CallTheme.colors.white,
                contentColor = CallTheme.colors.gray900,
            ),
            border = BorderStroke(
                width = 1.dp,
                color = CallTheme.colors.gray100,
            ),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = "시간 변경",
                style = CallTheme.typography.bodySmallBold,
            )
        }


    }
}

/**
 * 통화 약속 Preview
 * - 약속이 한 건 있는 상태 확인
 */
@Preview(showBackground = true, widthDp = 412)
@Composable
private fun HomeReservationCardPreview() {
    CallFromAiTheme {
        HomeReservationCard(
            reservation = HomeReservationUiModel(
                hasReservation = true,
                reservationCountText = "약속 1건",
                profileImageUrl = null,
                firstName = "민준",
                scheduledAtText = "오늘 오후 9시",
                scheduledTimeText = "21:00",
            ),
        )
    }
}

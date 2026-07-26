package kr.co.call.impl.component.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.component.ProfileImage
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.domain.model.home.NotificationType
import kr.co.call.impl.component.UnreadIndicator
import kr.co.call.impl.mapper.toUiModel
import kr.co.call.impl.viewmodel.model.HomeNotificationUiModel

/**
 * 알림 Paging 데이터를 Compose 목록 아이템으로 변환
 */
@Composable
fun NotificationPagingContent(
    notifications: Flow<PagingData<HomeNotification>>,
    content: @Composable (LazyPagingItems<HomeNotification>) -> Unit,
) {
    val lazyNotificationItems = notifications.collectAsLazyPagingItems()
    content(lazyNotificationItems)
}

/**
 * 알림 리스트 안내 문구
 */
@Composable
fun NotificationListHeader(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "최근 7일 간의 알림만 표시됩니다.",
        modifier = modifier.padding(
            start = 16.dp,
            top = 16.dp,
            bottom = 18.dp,
        ),
        color = CallTheme.colors.gray800,
        style = CallTheme.typography.bodySmall,
    )
}

/**
 * 홈 LazyColumn에 Paging 알림 아이템 추가
 */
fun LazyListScope.notificationItems(
    notifications: LazyPagingItems<HomeNotification>,
    onCallClick: (characterName: String) -> Unit,
) {
    items(
        count = notifications.itemCount,
        key = notifications.itemKey { notification -> notification.notificationId },
    ) { index ->
        notifications[index]?.let { notification ->
            NotificationCard(
                notificationState = notification.toUiModel(),
                onCallClick = {
                    notification.characterName?.let(onCallClick)
                },
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp,
                ),
            )
        }
    }

    item {
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * 알림 내용 포함하는 공통 카드 컴포넌트
 */
@Composable
private fun NotificationCard(
    notificationState: HomeNotificationUiModel,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(20.dp)
    val cardHeight = when (notificationState.type) {
        NotificationType.CALL_RESERVATION -> 88.dp
        NotificationType.ANNIVERSARY -> 109.dp
        NotificationType.MISSED_CALL -> 129.dp
    }
    val borderColor = when {
        notificationState.type == NotificationType.ANNIVERSARY -> CallTheme.colors.gray100
        notificationState.isRead -> CallTheme.colors.gray100
        else -> CallTheme.colors.mainVariant2
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .background(
                color = CallTheme.colors.white,
                shape = cardShape,
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = cardShape,
            ),
    ) {
        // 기념일, 예약통화, 부재중 통화에 따라 분기
        when (notificationState.type) {
            NotificationType.ANNIVERSARY -> {
                AnniversaryNotificationContent(
                    title = notificationState.type.title,
                    content = notificationState.content,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 17.dp,
                            top = 23.dp,
                            end = 17.dp,
                        ),
                )
            }

            NotificationType.CALL_RESERVATION -> {
                CallNotificationContent(
                    title = notificationState.type.title,
                    content = notificationState.content,
                    profileImageUrl = notificationState.profileImageUrl,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 17.dp,
                            top = 21.dp,
                            end = 17.dp,
                        ),
                )
            }

            NotificationType.MISSED_CALL -> {
                CallNotificationContent(
                    title = notificationState.type.title,
                    content = notificationState.content,
                    profileImageUrl = notificationState.profileImageUrl,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 17.dp,
                            top = 21.dp,
                            end = 17.dp,
                        ),
                )
            }
        }

        if (notificationState.type == NotificationType.MISSED_CALL) {
            // 부재중이라면 다시 전화하기 버튼 표시
            Button(
                onClick = onCallClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 17.dp,
                        bottom = 20.dp,
                    )
                    .width(290.dp)
                    .height(32.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CallTheme.colors.mainVariant2,
                    contentColor = CallTheme.colors.black,
                ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_home_call_btn,
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(
                        width = 13.dp,
                        height = 12.dp,
                    ),
                    tint = CallTheme.colors.mainVariant1,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "다시 전화하기",
                    style = CallTheme.typography.captionBold,
                )
            }
        }

        // n분 전 표시
        Text(
            text = notificationState.createdAtText,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 20.dp,
                    end = 17.dp,
                ),
            color = CallTheme.colors.gray400,
            style = CallTheme.typography.caption,
        )

        // 알림 읽음 여부 표시
        if (!notificationState.isRead) {
            UnreadIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 10.dp,
                        end = 11.dp,
                    ),
            )
        }

    }
}

@Composable
private fun AnniversaryNotificationContent(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // 기념일 아이콘
        Box(
            modifier = Modifier
                .size(41.dp)
                .background(
                    color = CallTheme.colors.mainVariant2,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDDD3\uFE0F",
                fontSize = 24.sp,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = title,
                color = CallTheme.colors.black,
                style = CallTheme.typography.bodySmallBold,
            )
            Text(
                text = content,
                color = CallTheme.colors.gray600,
                style = CallTheme.typography.bodySmall,
            )
        }
    }
}

/**
 * 통화 약속, 예약 통화 타입 시 사용하는 공통 컴포넌트
 */
@Composable
private fun CallNotificationContent(
    title: String,
    content: String,
    profileImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        ProfileImage(
            profileImageUrl = profileImageUrl,
            showCallBadge = true,
        )

        Column(
            modifier = Modifier.padding(top = 1.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = CallTheme.colors.black,
                style = CallTheme.typography.bodySmallBold,
            )
            Text(
                text = content,
                color = CallTheme.colors.gray600,
                style = CallTheme.typography.bodySmall,
            )
        }

    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun NotificationCardPreview() {
    val now = LocalDateTime.now()

    CallFromAiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NotificationCard(
                notificationState = HomeNotification(
                    notificationId = 1,
                    type = NotificationType.CALL_RESERVATION,
                    title = "통화 약속",
                    content = "오늘 밤 9시 통화 15분 뒤에 시작됩니다.",
                    isRead = false,
                    createdAt = now.minusMinutes(1),
                    characterName = "민준",
                    profileImageUrl = null,
                ).toUiModel(),
                onCallClick = {},
            )

            NotificationCard(
                notificationState = HomeNotification(
                    notificationId = 2,
                    type = NotificationType.MISSED_CALL,
                    title = "부재중 전화",
                    content = "민준이에게서 받지 못한 전화가 있어요.",
                    isRead = false,
                    createdAt = now.minusMinutes(30),
                    characterName = "민준",
                    profileImageUrl = null,
                ).toUiModel(),
                onCallClick = {},
            )

            NotificationCard(
                notificationState = HomeNotification(
                    notificationId = 3,
                    type = NotificationType.ANNIVERSARY,
                    title = "기념일",
                    content = "오늘은 민준이와 함께한지 30일 째!\n작은 기념일을 함께 축하해요 💗",
                    isRead = true,
                    createdAt = now.minusHours(1),
                    characterName = "민준",
                    profileImageUrl = null,
                ).toUiModel(),
                onCallClick = {},
            )
        }
    }
}

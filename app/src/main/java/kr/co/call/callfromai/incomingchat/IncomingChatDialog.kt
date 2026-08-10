package kr.co.call.callfromai.incomingchat

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.call.designsystem.component.ProfileImage
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kotlin.math.abs

/**
 * 수신된 AI 채팅 메시지를 화면 상단에 Dialog 형태로 표시합니다.
 *
 * 일정 시간 후 자동으로 닫히며, Dialog의 딤(dim) 효과는 제거됩니다.
 *
 * Dialog 클릭 시 채팅방으로 이동할 수 있으며, [isActionEnabled]가 false인 경우
 * 클릭 동작을 비활성화합니다.
 *
 * @param characterName 메시지를 보낸 캐릭터 이름
 * @param message 수신된 채팅 메시지
 * @param profileImageUrl 캐릭터 프로필 이미지 URL
 * @param onNavigateToChatRoom 채팅방으로 이동할 때 호출되는 콜백
 * @param onDismiss Dialog를 닫을 때 호출되는 콜백
 * @param durationMillis Dialog 표시 시간(ms). 기본값은 3초
 * @param receivedAtText 메시지 수신 시각 텍스트
 * @param isActionEnabled 채팅방 이동 클릭 활성화 여부
 */
@Composable
fun IncomingChatDialog(
    characterName: String,
    message: String,
    profileImageUrl: String?,
    onNavigateToChatRoom: () -> Unit,
    onDismiss: () -> Unit,
    durationMillis: Long = 4_500L,
    receivedAtText: String = "방금 전",
    isActionEnabled: Boolean = true,
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(characterName, message, durationMillis) {
        if (durationMillis > 0L) {
            delay(durationMillis)
            currentOnDismiss()
        }
    }

    Dialog(
        onDismissRequest = currentOnDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val view = LocalView.current

        // 딤 효과 제거 + 카드 영역 밖 터치가 아래 화면으로 통과되도록 설정
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.apply {
                setDimAmount(0f)
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                // 윈도우 영역(카드) 밖 터치는 아래 화면으로 통과
                addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                // fillMaxSize 없이도 카드가 상단에 위치하도록
                setGravity(Gravity.TOP)
            }
        }

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    top = 9.dp,
                    end = 16.dp,
                ),
        ) {
            IncomingChatDialogContent(
                characterName = characterName,
                message = message,
                profileImageUrl = profileImageUrl,
                receivedAtText = receivedAtText,
                onNavigateToChatRoom = onNavigateToChatRoom,
                onDismiss = currentOnDismiss,
                isActionEnabled = isActionEnabled,
            )
        }
    }
}

@Composable
private fun IncomingChatDialogContent(
    characterName: String,
    message: String,
    profileImageUrl: String?,
    receivedAtText: String,
    onNavigateToChatRoom: () -> Unit,
    onDismiss: () -> Unit,
    isActionEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val dismissThresholdPx = with(LocalDensity.current) { 100.dp.toPx() }

    Column(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                alpha = (1f - abs(offsetX.value) / (dismissThresholdPx * 2.5f)).coerceIn(0f, 1f)
                shadowElevation = 15.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = false
            }
            .fillMaxWidth()
            .widthIn(max = 380.dp)
            .background(
                color = CallTheme.colors.background,
                shape = RoundedCornerShape(20.dp),
            )
            .border(
                width = 1.dp,
                color = CallTheme.colors.mainVariant5Chat,
                shape = RoundedCornerShape(20.dp),
            )
            .noRippleClickable(
                enabled = isActionEnabled,
                onClickLabel = "채팅방으로 이동하기",
                role = Role.Button,
                onClick = onNavigateToChatRoom,
            )
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch { offsetX.snapTo(offsetX.value + delta) }
                },
                onDragStopped = {
                    if (abs(offsetX.value) > dismissThresholdPx) {
                        scope.launch {
                            offsetX.animateTo(if (offsetX.value > 0) 1000f else -1000f)
                            onDismiss()
                        }
                    } else {
                        scope.launch { offsetX.animateTo(0f) }
                    }
                },
            )
            .padding(
                start = 23.dp,
                top = 18.dp,
                end = 24.dp,
                bottom = 22.dp,
            ),
    ) {
        Text(
            text = "전화왔어",
            color = CallTheme.colors.mainVariant1,
            style = CallTheme.typography.bodySmallBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.Top) {
            ProfileImage(
                profileImageUrl = profileImageUrl,
                size = DpSize(50.dp, 50.dp),
                showCallBadge = true,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = characterName,
                        color = CallTheme.colors.gray900,
                        style = CallTheme.typography.titleSmallBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = receivedAtText,
                        color = CallTheme.colors.gray400,
                        style = CallTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.offset(y = (-3).dp),
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = message,
                    color = CallTheme.colors.gray900,
                    style = CallTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun IncomingChatDialogContentPreview() {
    CallFromAiTheme {
        IncomingChatDialogContent(
            characterName = "AI 캐릭터",
            message = "오늘 하루는 어땠어? 😊",
            profileImageUrl = null,
            receivedAtText = "방금 전",
            onNavigateToChatRoom = {},
            onDismiss = {},
            isActionEnabled = true,
        )
    }
}

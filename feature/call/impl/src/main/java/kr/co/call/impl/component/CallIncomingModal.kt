package kr.co.call.impl.component

import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.ProfileImage
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun IncomingCallDialog(
    characterName: String,
    profileImageUrl: String?,
    onNavigateToChatRoom: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isActionEnabled: Boolean,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val view = LocalView.current

        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.apply {
                setDimAmount(0.4f)
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    top = 9.dp,
                    end = 16.dp,
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            IncomingCallDialogContent(
                characterName = characterName,
                profileImageUrl = profileImageUrl,
                onNavigateToChatRoom = onNavigateToChatRoom,
                onAccept = onAccept,
                onReject = onReject,
                isActionEnabled = isActionEnabled,
            )
        }
    }
}

@Composable
private fun IncomingCallDialogContent(
    characterName: String,
    profileImageUrl: String?,
    onNavigateToChatRoom: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isActionEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp)
            .height(209.dp)
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .background(
                color = CallTheme.colors.background,
                shape = RoundedCornerShape(20.dp),
            )
            .border(
                width = 1.dp,
                color = CallTheme.colors.mainVariant5Chat,
                shape = RoundedCornerShape(20.dp),
            ),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 23.dp,
                    top = 18.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "전화왔어",
                color = CallTheme.colors.mainVariant1,
                style = CallTheme.typography.bodySmallBold,
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 9.dp)
                    .width(1.dp)
                    .height(15.dp)
                    .background(CallTheme.colors.mainVariant1),
            )
            Text(
                text = "수신전화",
                color = CallTheme.colors.gray400,
                style = CallTheme.typography.bodySmall,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 23.dp,
                    top = 43.dp,
                )
                .size(75.dp),
        ) {
            ProfileImage(
                profileImageUrl = profileImageUrl,
                size = DpSize(68.dp, 68.dp),
                modifier = Modifier.align(Alignment.TopStart),
            )
            Image(
                painter = painterResource(R.drawable.img_call_logo),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(25.dp),
            )
        }

        Text(
            text = characterName,
            color = CallTheme.colors.gray900,
            style = CallTheme.typography.titleSmallBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 109.dp,
                    top = 65.dp,
                    end = 24.dp,
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 22.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChatRoomNavigationButton(
                onClick = onNavigateToChatRoom,
                enabled = isActionEnabled,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(27.dp))
            IncomingCallButton(
                iconRes = R.drawable.ic_call_accept_icon,
                backgroundColor = CallTheme.colors.callAccept,
                contentDescription = "전화 받기",
                onClick = onAccept,
                enabled = isActionEnabled,
                buttonSize = 54.dp,
                iconSize = 24.dp,
            )
            Spacer(modifier = Modifier.width(25.dp))
            IncomingCallButton(
                iconRes = R.drawable.ic_call_reject_icon,
                backgroundColor = CallTheme.colors.callDecline,
                contentDescription = "전화 거절하기",
                onClick = onReject,
                enabled = isActionEnabled,
                buttonSize = 54.dp,
                iconSize = 32.dp,
            )
        }
    }
}

@Composable
private fun ChatRoomNavigationButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(CallTheme.colors.white)
            .alpha(if (enabled) 1f else 0.38f)
            .noRippleClickable(
                enabled = enabled,
                onClickLabel = "채팅방으로 이동하기",
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "채팅방으로 이동하기",
            color = CallTheme.colors.gray400,
            style = CallTheme.typography.bodySmallBold,
        )
    }
}

@Composable
fun IncomingCallActions(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
    ) {
        IncomingCallButton(
            iconRes = R.drawable.ic_call_accept_icon,
            backgroundColor = CallTheme.colors.callAccept,
            contentDescription = "전화 받기",
            onClick = onAccept,
            enabled = enabled,
            buttonSize = 80.dp,
            iconSize = 40.dp,
        )
        Spacer(modifier = Modifier.weight(1f))
        IncomingCallButton(
            iconRes = R.drawable.ic_call_reject_icon,
            backgroundColor = CallTheme.colors.callDecline,
            contentDescription = "전화 거절하기",
            onClick = onReject,
            enabled = enabled,
            buttonSize = 80.dp,
            iconSize = 40.dp,
        )
    }
}

@Composable
private fun IncomingCallButton(
    iconRes: Int,
    backgroundColor: Color,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
    buttonSize: Dp,
    iconSize: Dp,
) {
    Box(
        modifier = Modifier
            .size(buttonSize)
            .shadow(
                elevation = 15.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .alpha(if (enabled) 1f else 0.38f)
            .noRippleClickable(
                enabled = enabled,
                onClickLabel = contentDescription,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun IncomingCallDialogPreview() {
    CallFromAiTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CallTheme.colors.background)
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(
                    start = 16.dp,
                    top = 49.dp,
                    end = 16.dp,
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            IncomingCallDialogContent(
                characterName = "민준",
                profileImageUrl = null,
                onNavigateToChatRoom = {},
                onAccept = {},
                onReject = {},
                isActionEnabled = true,
            )
        }
    }
}
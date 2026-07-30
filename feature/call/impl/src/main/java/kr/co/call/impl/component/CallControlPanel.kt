package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 통화 중일때 표시되는 패널
 * - 음소거 , 스피커 on off
 * - 통화 종료
 */
@Composable
fun CallControlPanel(
    isMicrophoneEnabled: Boolean,
    isSpeakerEnabled: Boolean,
    onMicrophoneClick: () -> Unit,
    onEndCallClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp)
            .height(136.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(CallTheme.colors.white.copy(alpha = 0.6f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CallToggleControl(
            iconRes = if (isMicrophoneEnabled) {
                R.drawable.ic_call_mic_on
            } else {
                R.drawable.ic_call_mic_off
            },
            label = "음소거",
            isActive = !isMicrophoneEnabled,
            onClick = onMicrophoneClick,
            enabled = enabled,
        )
        EndCallButton(
            onClick = onEndCallClick,
            enabled = enabled,
        )
        CallToggleControl(
            iconRes = if (isSpeakerEnabled) {
                R.drawable.ic_call_speaker_on
            } else {
                R.drawable.ic_call_speaker_off
            },
            label = "스피커",
            isActive = isSpeakerEnabled,
            onClick = onSpeakerClick,
            enabled = enabled,
        )
    }
}

@Composable
private fun CallToggleControl(
    iconRes: Int,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val stateDescription = if (isActive) "켜짐" else "꺼짐"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) {
                        CallTheme.colors.gray900.copy(alpha = 0.08f)
                    } else {
                        Color.Transparent
                    },
                )
                .alpha(if (enabled) 1f else 0.38f)
                .noRippleClickable(
                    enabled = enabled,
                    onClickLabel = label,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    this.stateDescription = stateDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(33.dp),
            )
        }
        Text(
            text = label,
            color = CallTheme.colors.gray800,
            style = CallTheme.typography.bodySmall,
        )
    }
}

/**
 * 통화 종료 버튼
 */
@Composable
private fun EndCallButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(77.dp)
            .shadow(elevation = 10.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(CallTheme.colors.callDecline)
            .alpha(if (enabled) 1f else 0.38f)
            .noRippleClickable(
                enabled = enabled,
                onClickLabel = "통화 종료하기",
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_call_reject_icon),
            contentDescription = "통화 종료하기",
            tint = Color.White,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 175)
@Composable
private fun CallControlPanelPreview() {
    CallFromAiTheme {
        CallGradientBackground {
            CallControlPanel(
                isMicrophoneEnabled = true,
                isSpeakerEnabled = false,
                onMicrophoneClick = {},
                onEndCallClick = {},
                onSpeakerClick = {},
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

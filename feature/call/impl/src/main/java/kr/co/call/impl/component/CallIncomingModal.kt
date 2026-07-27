package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

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
        )
        Spacer(modifier = Modifier.weight(1f))
        IncomingCallButton(
            iconRes = R.drawable.ic_call_reject_icon,
            backgroundColor = CallTheme.colors.callDecline,
            contentDescription = "전화 거절하기",
            onClick = onReject,
            enabled = enabled,
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
) {
    Box(
        modifier = Modifier
            .size(80.dp)
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
            modifier = Modifier.size(40.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 112)
@Composable
private fun IncomingCallActionsPreview() {
    CallFromAiTheme {
        IncomingCallActions(
            onAccept = {},
            onReject = {},
            enabled = true,
        )
    }
}

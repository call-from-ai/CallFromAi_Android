package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun CircleIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (isPressed) {
                    CallTheme.colors.subPressed // 눌렸을 때
                } else {
                    CallTheme.colors.mainVariant1 // 기본
                }
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun CircleIconPreview() {
    CallFromAiTheme {
        CircleIcon(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_home_alarm),
                contentDescription = "카메라",
                tint = Color.Unspecified
            )
        }
    }
}
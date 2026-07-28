package kr.co.call.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 다크 배경의 하단 CTA 버튼 ( 배경, 텍스트 )
 */
@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconRes: Int? = null,
    containerColor: Color = CallTheme.colors.mainVariant4,
    contentColor: Color = CallTheme.colors.white,
    pressedContainerColor: Color = CallTheme.colors.subPressed3,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp)
            .background(
                color = when {
                    !enabled -> containerColor.copy(alpha = 0.38f)
                    isPressed -> pressedContainerColor
                    else -> containerColor
                },
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val contentTint = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)

        if (iconRes != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = contentTint,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = text, style = CallTheme.typography.bodyMediumMedium, color = contentTint)
            }
        } else {
            Text(text = text, style = CallTheme.typography.bodyMediumMedium, color = contentTint)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonPreview() {
    CallFromAiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(text = "다음", onClick = {})
            SecondaryButton(text = "다음", onClick = {}, enabled = false)
            SecondaryButton(
                text = "다음",
                onClick = {},
                containerColor = CallTheme.colors.subPressed3,
            )
        }
    }
}

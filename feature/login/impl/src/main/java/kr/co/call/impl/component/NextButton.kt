package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray200
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.MainVariant2

@Composable
fun NextButton (
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    containerColor: Color = MainVariant2,
    contentColor: Color = Black,
    pressedContainerColor: Color = CallTheme.colors.subPressed2,
    ){
    val interactionSource = remember { MutableInteractionSource()}
    val isPressed by interactionSource.collectIsPressedAsState()
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = when {
                        !enabled -> Gray200
                        isPressed -> pressedContainerColor
                        else -> containerColor
                    },
                    shape = RoundedCornerShape(20.dp),
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = CallTheme.typography.bodyLargeBold,
                color = if (enabled) contentColor else Gray600,
            )
        }
}

@Preview(
    showBackground = true,
)
@Composable
private fun NextButtonPreview() {
    CallFromAiTheme {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            NextButton(
                text = "다음",
                onClick = {},
                enabled = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            NextButton(
                text = "다음",
                onClick = {},
                enabled = false,
            )
        }
    }
}
package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun ChatBubblePopUp(
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    offset: IntOffset = IntOffset.Zero,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = offset,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .width(120.dp)
                .graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                    shape = RoundedCornerShape(12.dp)
                    clip = false
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CallTheme.colors.background),
        ) {
            PopUpItem(
                text = "복사",
                onClick = {
                    onCopy()
                    onDismiss()
                },
            )
            PopUpItem(
                text = "삭제",
                onClick = {
                    onDelete()
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun PopUpItem(
    text: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    Text(
        text = text,
        style = CallTheme.typography.bodySmall,
        color = CallTheme.colors.black,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .background(
                if (isPressed) CallTheme.colors.mainVariant3 else CallTheme.colors.white
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatBubblePopUpPreview() {
    CallFromAiTheme {
        ChatBubblePopUp(
            onCopy = {},
            onDelete = {},
            onDismiss = {},
        )
    }
}
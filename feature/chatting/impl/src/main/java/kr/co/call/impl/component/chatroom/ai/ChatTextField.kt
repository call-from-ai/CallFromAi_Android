package kr.co.call.impl.component.chatroom.ai

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.model.TextFieldState

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState(),
    onValueChange: (String) -> Unit = {},
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onRemoveImage: () -> Unit = {},
) {
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val hasImage = state.selectedImage != null

    // 이미지가 있으면 라운드 사각형, 없으면 기존 pill
    val containerShape = if (hasImage) RoundedCornerShape(24.dp) else RoundedCornerShape(50.dp)

    Column(
        modifier = modifier
            .background(
                color = CallTheme.colors.subGray,
                shape = containerShape,
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        // 선택된 이미지 미리보기 + 삭제 버튼
        if (hasImage) {
            Box(
                modifier = Modifier
                    .padding(start = 40.dp, top = 4.dp, bottom = 8.dp)
            ) {
                AsyncImage(
                    model = state.selectedImage,
                    contentDescription = "선택된 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(121.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )

                Icon(
                    painter = painterResource(R.drawable.ic_chat_close),
                    contentDescription = "이미지 삭제",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-9).dp, y = 9.dp)
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRemoveImage,
                        ),
                )
            }

            HorizontalDivider(
                color = CallTheme.colors.gray200,
                thickness = 1.dp,
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        // 기존 입력 Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIcon(
                painter = painterResource(R.drawable.ic_chat_camera),
                contentDescription = "카메라",
                onClick = onCameraClick,
            )

            BasicTextField(
                value = state.text,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                textStyle = CallTheme.typography.bodySmall.copy(
                    color = CallTheme.colors.black,
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (state.text.isEmpty()) {
                            Text(
                                text = "메시지를 입력하세요",
                                style = CallTheme.typography.bodySmall,
                                color = CallTheme.colors.gray400,
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // 이미지가 있으면 전송 버튼을 항상 노출 (키보드 없어도 보낼 수 있게)
            if (isKeyboardVisible || hasImage) {
                CircleIcon(
                    painter = painterResource(R.drawable.ic_chat_send),
                    contentDescription = "전송",
                    onClick = onSendClick,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_chat_gallery),
                    contentDescription = "갤러리",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = (-12).dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onGalleryClick,
                        ),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChatTextFieldPreview() {
    CallFromAiTheme {
        ChatTextField(
            state = TextFieldState(text = ""),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChatTextFieldWithImagePreview() {
    CallFromAiTheme {
        ChatTextField(
            state = TextFieldState(text = "", selectedImage = Uri.EMPTY),
        )
    }
}

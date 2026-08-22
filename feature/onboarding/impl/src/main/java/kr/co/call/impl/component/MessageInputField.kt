package kr.co.call.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.SubGray
import kr.co.call.onboarding.impl.R
import kr.co.call.designsystem.modifier.noRippleClickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kr.co.call.designsystem.theme.CallFromAiTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kr.co.call.designsystem.theme.Gray600

@Composable
fun MessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    autoFocus: Boolean = false,
){
    val maxLength=10
    val focusManager= LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember {
        FocusRequester()
    }
    val canSend=value.isNotBlank() && value.length <= maxLength
    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Row(
        modifier=modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color=SubGray,
                shape= RoundedCornerShape(50.dp)
            )
            .padding(
                start=23.dp,
                top=4.dp,
                end=4.dp,
                bottom=4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ){
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = CallTheme.typography.bodySmallBold.copy(
                color = Gray900,
            ),
            cursorBrush = SolidColor(MainVariant1),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send,
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (canSend) {
                        onSendClick()
                        focusManager.clearFocus()
                    }
                },
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder.isNotBlank()) {
                        Text(
                            text = placeholder,
                            color = Gray900,
                            style = CallTheme.typography.bodySmallBold,
                        )
                    }

                    innerTextField()
                }
            },
        )
        Text(
            text = "${value.length}/$maxLength",
            color = if (value.isEmpty() || value.length>maxLength) {
                Gray600
            } else {
                MainVariant1
            },
            style = CallTheme.typography.bodySmallBold,
        )
        Spacer(modifier = Modifier.width(5.dp))

        Image(
            painter = painterResource(R.drawable.spendbutton),
            contentDescription = "전송",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .noRippleClickable(
                    enabled = canSend,
                    role = Role.Button,
                    onClick = onSendClick,
                ),
        )
    }
}

@Preview(
    name = "10글자 미만",
    showBackground = true,
    widthDp = 390,
)
@Composable
private fun MessageInputFieldUnderLimitPreview() {
    var message by remember {
        mutableStateOf("안녕하세요") // 5글자
    }

    CallFromAiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            MessageInputField(
                value = message,
                onValueChange = { message = it },
                onSendClick = { message = "" },
                placeholder = "메시지를 입력해주세요.",
            )
        }
    }
}

@Preview(
    name = "10글자 초과",
    showBackground = true,
    widthDp = 390,
)
@Composable
private fun MessageInputFieldOverLimitPreview() {
    var message by remember {
        mutableStateOf("안녕하세요반갑습니다!") // 11글자
    }

    CallFromAiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            MessageInputField(
                value = message,
                onValueChange = { message = it },
                onSendClick = { message = "" },
                placeholder = "메시지를 입력해주세요.",
            )
        }
    }
}

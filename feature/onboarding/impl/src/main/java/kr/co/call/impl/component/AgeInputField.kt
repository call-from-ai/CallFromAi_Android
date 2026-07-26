package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.SubRed
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AgeInputField(
    age: String,
    onAgeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier) {
        Row {
            Text(
                text = "만 나이",
                style = CallTheme.typography.bodyMedium,
                color = Gray600,
            )
            Text(
                text = "*",
                style = CallTheme.typography.bodyMedium,
                color = SubRed,
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        BasicTextField(
            value = age,
            onValueChange = { input ->
                if (input.all(Char::isDigit)) onAgeChange(input)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .background(
                    color = Gray100,
                    shape = RoundedCornerShape(10.dp),
                ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            textStyle = CallTheme.typography.bodyMediumMedium.copy(
                color = Gray900,
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 19.dp,
                            top = 16.dp,
                            end = 15.dp,
                            bottom = 15.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    Text(
                        text = "(세)",
                        style = CallTheme.typography.bodyMedium,
                        color = Gray600,
                    )
                }
            },
        )
    }
}

@Preview(
    name = "AgeInputField",
    showBackground = true,
    widthDp = 393,
)
@Composable
private fun AgeInputFieldPreview() {
    var age by rememberSaveable { mutableStateOf("25") }
    CallFromAiTheme {
        AgeInputField(
            age = age,
            onAgeChange = { age = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}
package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 성/이름 입력란
 */
@Composable
fun EditProfileNameFields(
    lastName: String,
    firstName: String,
    onLastNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EditProfileSingleNameField(
            label = "성",
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = "성",
            modifier = Modifier.weight(1f),
        )
        EditProfileSingleNameField(
            label = "이름",
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = "이름",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun EditProfileSingleNameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = CallTheme.typography.bodyMedium,
            color = CallTheme.colors.gray600,
        )
        Spacer(modifier = Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .background(
                    color = CallTheme.colors.gray100,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = CallTheme.typography.bodyMedium.copy(
                    color = CallTheme.colors.gray900,
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = CallTheme.typography.bodyMedium,
                            color = CallTheme.colors.gray400,
                        )
                    }
                    inner()
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun EditProfileNameFieldsPreview() {
    CallFromAiTheme {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            EditProfileNameFields(
                lastName = "",
                firstName = "",
                onLastNameChange = {},
                onFirstNameChange = {},
            )
        }
    }
}

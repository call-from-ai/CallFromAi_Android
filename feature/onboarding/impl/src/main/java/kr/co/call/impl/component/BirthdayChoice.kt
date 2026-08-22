package kr.co.call.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.SubRed
import kr.co.call.onboarding.impl.R
import kr.co.call.designsystem.modifier.noRippleClickable
import java.time.LocalDate
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun BirthdayChoice(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = true,
) {
    var showBottomWheel by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "생년월일",
                color = Gray600,
                style = CallTheme.typography.bodyMedium,
            )
            if (required) {
                Text(
                    text = " *",
                    color = SubRed,
                    style = CallTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier=Modifier.height(7.dp))

        Surface(
            modifier= Modifier
                .fillMaxWidth()
                .height(55.dp)
                .noRippleClickable(
                    role=Role.Button,
                    onClick = { showBottomWheel = true },
                ),
            color=Gray100,
            shape=RoundedCornerShape(10.dp),
        ){
            Row(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal=16.dp),
                verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                Text(
                    text = selectedDate?.toDisplayText()
                        ?: "생년월일을 입력해주세요",
                    color = if (selectedDate == null) Gray600 else Gray900,
                    style = CallTheme.typography.bodyMediumMedium,
                )

                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "생년월일 선택",
                    modifier = Modifier.size(
                        height=20.dp,
                        width=18.dp,),
                    tint = Color.Unspecified,
                )
            }
        }
    }

    if (showBottomWheel) {
        BottomWheel(
            selectedDate = selectedDate,
            onConfirmClick = { newDate ->
                if(newDate != null) {
                    onDateSelected(newDate)
                }
                showBottomWheel = false
            },
            onDismissRequest = {
                showBottomWheel = false
            },
        )
    }
}

private fun LocalDate.toDisplayText(): String {
    return "$year / $monthValue / $dayOfMonth"
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun BirthdayChoicePreview() {
    CallFromAiTheme {
        BirthdayChoice(
            selectedDate = null,
            onDateSelected = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )
    }
}

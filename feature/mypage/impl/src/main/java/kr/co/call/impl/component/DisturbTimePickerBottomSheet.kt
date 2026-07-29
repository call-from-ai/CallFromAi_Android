package kr.co.call.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kr.co.call.designsystem.component.bottomsheet.ConfirmBottomSheet
import kr.co.call.designsystem.component.picker.WheelPicker
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 방해 금지 시작/종료 시간 선택 바텀시트
 */
@Composable
fun DisturbTimePickerBottomSheet(
    title: String,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val times = remember {
        List(48) { index -> LocalTime.MIN.plusMinutes(index * 30L) }
    }
    val initialIndex = times.indexOf(selectedTime).takeIf { it >= 0 } ?: 0
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
    )
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    ConfirmBottomSheet(
        title = title,
        onConfirmClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-22).dp)
                    .padding(horizontal = 100.dp),
                color = CallTheme.colors.gray200,
            )
            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 22.dp)
                    .padding(horizontal = 100.dp),
                color = CallTheme.colors.gray200,
            )

            WheelPicker(
                items = times,
                listState = listState,
                itemText = { time -> time.format(timeFormatter) },
                onItemSelected = { _, time -> onTimeSelected(time) },
                modifier = Modifier.width(120.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 200)
@Composable
private fun DisturbTimeWheelPickerPreview() {
    CallFromAiTheme {
        val times = remember {
            List(48) { index -> LocalTime.MIN.plusMinutes(index * 30L) }
        }
        val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            WheelPicker(
                items = times,
                listState = rememberLazyListState(initialFirstVisibleItemIndex = 18),
                itemText = { time -> time.format(timeFormatter) },
                modifier = Modifier.width(120.dp),
            )
        }
    }
}

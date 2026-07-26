package kr.co.call.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kr.co.call.designsystem.component.bottomsheet.ConfirmBottomSheet
import kr.co.call.designsystem.component.picker.WheelPicker
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun TimeChangeBottomSheet(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 월 경계를 포함한 연속 날짜와 30분 단위 시간을 생성
    val today = remember { LocalDate.now() }
    val dates = remember(today) {
        (-30L..90L).map { dayOffset ->
            today.plusDays(dayOffset)
        }
    }
    val times = remember {
        List(48) { index -> LocalTime.MIN.plusMinutes(index * 30L) }
    }

    // 상태의 선택값을 휠 초기 위치로 사용
    val initialDateIndex = dates.indexOf(selectedDate).takeIf { index -> index >= 0 }
        ?: 30
    val initialTimeIndex = times.indexOf(selectedTime).takeIf { index -> index >= 0 } ?: 0
    val dateListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialDateIndex,
    )
    val timeListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialTimeIndex,
    )

    ConfirmBottomSheet(
        title = "시간 변경",
        onConfirmClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        TimeWheelPicker(
            dates = dates,
            times = times,
            dateListState = dateListState,
            timeListState = timeListState,
            onDateSelected = onDateSelected,
            onTimeSelected = onTimeSelected,
        )
    }
}

/**
 * 시간 변경 바텀시트의 날짜, 시간 휠 피커
 */
@Composable
private fun TimeWheelPicker(
    dates: List<LocalDate>,
    times: List<LocalTime>,
    dateListState: LazyListState,
    timeListState: LazyListState,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 39.dp
    val visibleItemCount = 5
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * visibleItemCount),
    ) {
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-22).dp)
                .padding(horizontal = 35.dp),
            color = CallTheme.colors.gray200,
        )
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 22.dp)
                .padding(horizontal = 35.dp),
            color = CallTheme.colors.gray200,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * visibleItemCount)
                .padding(horizontal = 35.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            // 날짜 휠 피커
            WheelPicker(
                items = dates,
                listState = dateListState,
                itemText = { date -> "${date.monthValue}월 ${date.dayOfMonth}일" },
                itemHeight = itemHeight,
                visibleItemCount = visibleItemCount,
                onItemSelected = { _, date -> onDateSelected(date) },
                modifier = Modifier.width(150.dp),
            )

            // 시간 휠 피커
            WheelPicker(
                items = times,
                listState = timeListState,
                itemText = { time -> time.format(timeFormatter) },
                itemHeight = itemHeight,
                visibleItemCount = visibleItemCount,
                onItemSelected = { _, time -> onTimeSelected(time) },
                modifier = Modifier.width(120.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 195)
@Composable
private fun TimeWheelPickerPreview() {
    CallFromAiTheme {
        val selectedDate = LocalDate.of(2025, 3, 15)
        val dates = remember {
            (-30L..90L).map { dayOffset ->
                selectedDate.plusDays(dayOffset)
            }
        }
        val times = remember {
            List(48) { index ->
                LocalTime.MIN.plusMinutes(index * 30L)
            }
        }

        TimeWheelPicker(
            dates = dates,
            times = times,
            dateListState = rememberLazyListState(initialFirstVisibleItemIndex = 30),
            timeListState = rememberLazyListState(initialFirstVisibleItemIndex = 20),
            onDateSelected = {},
            onTimeSelected = {},
        )
    }
}

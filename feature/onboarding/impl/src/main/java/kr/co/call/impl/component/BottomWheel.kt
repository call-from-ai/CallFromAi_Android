package kr.co.call.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallTheme
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.component.bottomsheet.ConfirmBottomSheet
import kr.co.call.designsystem.component.picker.WheelPicker
import kr.co.call.designsystem.theme.CallFromAiTheme

@Composable
fun BottomWheel(
    selectedDate: LocalDate,
    onConfirmClick: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
){
    val years = remember {
        (1900..LocalDate.now().year).toList()
    }

    var draftDate by remember(selectedDate) {
        mutableStateOf(selectedDate)
    }

    val yearState = rememberLazyListState(
        initialFirstVisibleItemIndex = years
            .indexOf(selectedDate.year)
            .coerceAtLeast(0),
    )
    val monthState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedDate.monthValue - 1,
    )
    val dayState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedDate.dayOfMonth - 1,
    )

    ConfirmBottomSheet(
        title = "생년월일 선택",
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        onConfirmClick = {
            onConfirmClick(draftDate)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(195.dp),
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
                    .padding(horizontal = 35.dp),
            ) {
                WheelPicker(
                    items = years,
                    listState = yearState,
                    itemText = { "${it}년" },
                    onItemSelected = { _, year ->
                        draftDate = LocalDate.of(
                            year,
                            draftDate.monthValue,
                            draftDate.dayOfMonth.coerceAtMost(
                                YearMonth.of(
                                    year,
                                    draftDate.monthValue,
                                ).lengthOfMonth(),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                WheelPicker(
                    items = (1..12).toList(),
                    listState = monthState,
                    itemText = { "%02d월".format(it) },
                    onItemSelected = { _, month ->
                        draftDate = LocalDate.of(
                            draftDate.year,
                            month,
                            draftDate.dayOfMonth.coerceAtMost(
                                YearMonth.of(
                                    draftDate.year,
                                    month,
                                ).lengthOfMonth(),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                WheelPicker(
                    items = (
                            1..YearMonth
                                .of(
                                    draftDate.year,
                                    draftDate.monthValue,
                                )
                                .lengthOfMonth()
                            ).toList(),
                    listState = dayState,
                    itemText = { "%02d일".format(it) },
                    onItemSelected = { _, day ->
                        draftDate = draftDate.withDayOfMonth(day)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}


@Preview(
    name = "생년월일 선택",
    showBackground = true,
    widthDp = 412,
    heightDp = 800,
)
@Composable
private fun BottomWheelPreview() {
    CallFromAiTheme {
        BottomWheel(
            selectedDate = LocalDate.of(2001, 5, 18),
            onConfirmClick = {},
            onDismissRequest = {},
        )
    }
}
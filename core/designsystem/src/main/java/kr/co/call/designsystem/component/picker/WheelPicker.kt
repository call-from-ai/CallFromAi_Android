package kr.co.call.designsystem.component.picker

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 목록을 세로로 스크롤해 가운데 항목을 선택하는 공통 휠 피커입니다.
 *
 * 이 컴포넌트는 날짜나 시간 같은 값의 의미를 알지 않습니다. 호출부가 [items]와
 * [itemText]를 제공하면 스크롤, 중앙 항목 계산, 스냅, 글자 크기와 색상 표현을 담당합니다.
 * 따라서 화면마다 필요한 휠 개수가 다르면 이 컴포넌트를 하나 이상 조합해서 사용합니다.
 *
 * 글자 크기는 항목의 실제 화면 위치를 기준으로 계산합니다. 중앙에서 한 칸 거리까지는
 * `titleSmall` 크기를 유지하고, 한 칸과 두 칸 사이에서는 `bodyLarge` 크기까지 연속으로
 * 줄어듭니다. 두 칸 이상 떨어진 항목은 `bodyLarge` 크기를 유지하므로 스크롤 중 크기가
 * 단계적으로 바뀌지 않습니다. 위아래 색상은 [gradientTint]로 합성합니다.
 *
 * 날짜 한 개를 선택하는 예시:
 * ```kotlin
 * val dates = remember {
 *     (-30L..90L).map(LocalDate.now()::plusDays)
 * }
 * val dateListState = rememberLazyListState(initialFirstVisibleItemIndex = 30)
 *
 * WheelPicker(
 *     items = dates,
 *     listState = dateListState,
 *     itemText = { date -> "${date.monthValue}월 ${date.dayOfMonth}일" },
 *     onItemSelected = { _, date -> onDateSelected(date) },
 * )
 * ```
 *
 * 날짜와 시간처럼 두 개 이상의 휠이 필요한 경우에는 각 화면에서 배치만 결정합니다.
 * ```kotlin
 * Row {
 *     WheelPicker(
 *         items = dates,
 *         listState = dateListState,
 *         itemText = ::formatDate,
 *         onItemSelected = { _, date -> onDateSelected(date) },
 *         modifier = Modifier.weight(1f),
 *     )
 *     WheelPicker(
 *         items = times,
 *         listState = timeListState,
 *         itemText = ::formatTime,
 *         onItemSelected = { _, time -> onTimeSelected(time) },
 *         modifier = Modifier.weight(1f),
 *     )
 * }
 * ```
 *
 * [listState]의 `initialFirstVisibleItemIndex`에 처음 선택할 항목의 인덱스를 지정합니다.
 * 위아래 여백이 선택 항목을 가운데에 배치하므로 별도의 중앙 인덱스 보정은 필요하지 않습니다.
 * [onItemSelected]는 최초 표시 시점과 스크롤이 멈춘 뒤에만 호출되어 ViewModel에 과도한
 * 이벤트가 전달되지 않습니다.
 *
 * @param items 휠에 표시할 원본 값 목록입니다. 빈 목록도 허용합니다.
 * @param listState 스크롤 위치를 소유하는 상태입니다. 초기 선택 위치와 외부 스크롤 제어에 사용합니다.
 * @param itemText 원본 값을 화면에 표시할 문자열로 변환합니다.
 * @param modifier 휠의 너비와 외부 배치를 지정합니다. 높이는 항목 높이와 노출 개수로 계산됩니다.
 * @param itemHeight 각 항목의 고정 높이입니다.
 * @param visibleItemCount 화면에 보일 항목 개수입니다. 중앙 행이 하나가 되도록 양의 홀수만 허용합니다.
 * @param onItemSelected 스크롤 종료 후 선택된 인덱스와 원본 값을 전달합니다.
 */
@Composable
fun <T> WheelPicker(
    items: List<T>,
    listState: LazyListState,
    itemText: (T) -> String,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 39.dp,
    visibleItemCount: Int = 5,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> },
) {
    require(visibleItemCount > 0 && visibleItemCount % 2 == 1) {
        "visibleItemCount must be a positive odd number."
    }

    val selectedIndex by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo
                .minByOrNull { itemInfo ->
                    val itemCenter = itemInfo.offset + itemInfo.size / 2
                    abs(itemCenter - viewportCenter)
                }
                ?.index
                ?: listState.firstVisibleItemIndex
        }
    }
    val currentOnItemSelected by rememberUpdatedState(onItemSelected)

    // 스크롤이 끝난 시점의 선택값만 외부에 전달
    LaunchedEffect(listState, items) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { isScrolling -> !isScrolling }
            .collect {
                items.getOrNull(selectedIndex)?.let { selectedItem ->
                    currentOnItemSelected(selectedIndex, selectedItem)
                }
            }
    }

    val centerPadding = itemHeight * (visibleItemCount / 2)

    LazyColumn(
        state = listState,
        modifier = modifier
            .height(itemHeight * visibleItemCount)
            .gradientTint(
                Brush.verticalGradient(
                    colors = listOf(
                        CallTheme.colors.gray200,
                        CallTheme.colors.gray400,
                        CallTheme.colors.black,
                        CallTheme.colors.gray400,
                        CallTheme.colors.gray200,
                    ),
                ),
            ),
        contentPadding = PaddingValues(vertical = centerPadding),
        flingBehavior = rememberSnapFlingBehavior(listState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(items) { index, item ->
            // 실제 중앙 거리로 글자 크기를 연속 보간
            val maximumFontSize = CallTheme.typography.titleSmall.fontSize.value
            val minimumFontSize = CallTheme.typography.bodyLarge.fontSize.value
            val fontSize by remember(listState, index, maximumFontSize, minimumFontSize) {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                    val itemInfo = layoutInfo.visibleItemsInfo
                        .firstOrNull { itemInfo -> itemInfo.index == index }
                        ?: return@derivedStateOf minimumFontSize
                    val itemCenter = itemInfo.offset + itemInfo.size / 2
                    val distanceInItems =
                        abs(itemCenter - viewportCenter) / itemInfo.size.coerceAtLeast(1).toFloat()
                    val shrinkProgress = (distanceInItems - 1f).coerceIn(0f, 1f)

                    maximumFontSize +
                        (minimumFontSize - maximumFontSize) * shrinkProgress
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = itemText(item),
                    style = CallTheme.typography.titleSmall.copy(
                        fontSize = fontSize.sp,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * 콘텐츠의 불투명 영역에 [brush] 색상을 합성합니다.
 *
 * 휠 피커에서는 위·아래 항목을 회색, 중앙 항목을 검은색으로 표현하는 데 사용합니다.
 * 별도의 레이어에서 콘텐츠를 먼저 그린 뒤 [blendMode]로 브러시를 합성하므로 텍스트 모양은
 * 유지하면서 위치별 색상만 변경됩니다.
 *
 * @param brush 콘텐츠에 합성할 색상 또는 그라데이션 브러시입니다.
 * @param blendMode 콘텐츠와 브러시를 합성하는 방식입니다. 기본값은 콘텐츠 영역에만 색상을
 * 적용하는 [BlendMode.SrcIn]입니다.
 */
fun Modifier.gradientTint(
    brush: Brush,
    blendMode: BlendMode = BlendMode.SrcIn,
): Modifier = graphicsLayer(alpha = 0.99f)
    .drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = brush,
                blendMode = blendMode,
            )
        }
    }

@Preview(showBackground = true)
@Composable
private fun WheelPickerPreview() {
    CallFromAiTheme {
        WheelPicker(
            items = listOf("6월 29일", "6월 30일", "7월 1일", "7월 2일", "7월 3일"),
            listState = rememberLazyListState(initialFirstVisibleItemIndex = 2),
            itemText = { item -> item },
        )
    }
}

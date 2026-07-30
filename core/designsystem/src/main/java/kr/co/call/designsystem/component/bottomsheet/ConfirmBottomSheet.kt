package kr.co.call.designsystem.component.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 제목, 닫기 버튼, 확인 버튼을 공통으로 제공하는 바텀시트입니다.
 *
 * 화면별 콘텐츠는 [content] 슬롯에 전달합니다. 날짜 선택, 시간 선택 등 기능별 상태와
 * 이벤트는 호출부가 소유하며, 이 컴포넌트는 바텀시트의 공통 모양과 버튼 배치만 담당합니다.
 * 바깥 영역 터치와 시스템 뒤로가기는 모두 [onDismissRequest]로 전달됩니다.
 *
 * 기본 사용 예시:
 * ```kotlin
 * ConfirmBottomSheet(
 *     title = "시간 변경",
 *     onConfirmClick = onConfirmClick,
 *     onDismissRequest = onDismissRequest,
 * ) {
 *     TimeWheelPicker(...)
 * }
 * ```
 *
 * 바텀시트 기본 높이는 공통 규격 387dp입니다.
 * 본문이 커서 고정 높이 안에 안 들어가면 [sheetHeight]를 null로 두어
 * 콘텐츠 높이만큼 늘어나게 할 수 있습니다 (확인 버튼이 잘리지 않음).
 *
 * @param title 바텀시트 상단에 표시할 제목입니다.
 * @param onConfirmClick 하단 확인 버튼을 눌렀을 때 호출됩니다.
 * @param onDismissRequest 닫기 버튼, 바깥 영역 또는 시스템 뒤로가기로 닫을 때 호출됩니다.
 * @param modifier `ModalBottomSheet` 자체에 적용할 Modifier입니다.
 * @param confirmText 확인 버튼에 표시할 문구입니다.
 * @param sheetHeight 시트 전체 고정 높이. null이면 콘텐츠 wrap (기본 387dp).
 * @param content 제목과 확인 버튼 사이에 표시할 화면별 콘텐츠입니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    title: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
    sheetHeight: Dp? = 387.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = CallTheme.colors.white,
        scrimColor = Color.Black.copy(alpha = 0.2f),
        tonalElevation = 0.dp,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
    ) {
        ConfirmBottomSheetContent(
            title = title,
            confirmText = confirmText,
            onConfirmClick = onConfirmClick,
            onDismissRequest = onDismissRequest,
            sheetHeight = sheetHeight,
            content = content,
        )
    }
}

@Composable
internal fun ConfirmBottomSheetContent(
    title: String,
    confirmText: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    sheetHeight: Dp? = 387.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    // 고정 높이 모드: height() 후 인셋이면 콘텐츠 영역이 줄어 버튼이 잘릴 수 있음
    // 인셋/하단 패딩을 바깥에 두고, 본문은 heightIn(min)으로 하한만 맞춤 -> 전체 높이가 인셋만큼 늘어남
    // sheetHeight == null: 콘텐츠 wrap (프로필 피커 등)
    val heightModifier = if (sheetHeight != null) {
        Modifier.heightIn(min = sheetHeight)
    } else {
        Modifier.wrapContentHeight()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
            .then(heightModifier),
    ) {
        // 2줄 제목은 82dp 안에서 Bottom 정렬 시 상단이 시트 모서리에 붙음 -> 높이/상단 여백 확보
        val isMultiLineTitle = title.contains('\n')
        val headerHeight = if (isMultiLineTitle) 104.dp else 82.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .align(
                        if (isMultiLineTitle) Alignment.TopStart else Alignment.BottomStart,
                    )
                    .padding(
                        start = 16.dp,
                        end = 56.dp, // 닫기 버튼과 겹치지 않도록
                        top = if (isMultiLineTitle) 52.dp else 0.dp,
                        bottom = if (isMultiLineTitle) 0.dp else 24.dp,
                    ),
                style = CallTheme.typography.bodyLargeBold,
                color = CallTheme.colors.black,
            )

            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 5.dp)
                    .size(48.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_close_circle),
                    contentDescription = "닫기",
                    modifier = Modifier.size(27.dp),
                )
            }
        }

        content()

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = confirmText,
            onClick = onConfirmClick,
            modifier = Modifier.padding(horizontal = 29.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 387)
@Composable
private fun ConfirmBottomSheetPreview() {
    CallFromAiTheme {
        ConfirmBottomSheetContent(
            title = "시간 변경",
            confirmText = "확인",
            onConfirmClick = {},
            onDismissRequest = {},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .background(CallTheme.colors.gray100),
            )
        }
    }
}

package kr.co.call.designsystem.component

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 플로팅 바텀바의 실측 높이를 하위 컴포저블에 전달하는 CompositionLocal.
 *
 * 바텀바가 화면 위에 떠있는 구조(모서리 배경색 노출용)이기 때문에,
 * 스크롤 가능한 컨텐츠는 이 값만큼 bottom padding을 주어야 바텀바에 가려지지 않는다.
 *
 * 사용 예시:
 * ```kotlin
 * val bottomBarPadding = LocalBottomBarPadding.current
 * LazyColumn(contentPadding = PaddingValues(bottom = bottomBarPadding))
 * ```
 */
val LocalBottomBarPadding = compositionLocalOf<Dp> { 0.dp }

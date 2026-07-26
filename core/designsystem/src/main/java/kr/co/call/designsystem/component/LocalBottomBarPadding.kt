package kr.co.call.designsystem.component

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 플로팅 바텀바의 실측 높이를 하위 컴포저블에 전달하는 CompositionLocal.
 *
 * 바텀바가 화면 위에 떠있는 구조(모서리 배경색 노출용)이기 때문에(요구사항.....하...),
 * 스크롤 가능한 컨텐츠는 이 값만큼 bottom padding을 주어야 바텀바에 가려지지 않는다.
 *
 * 사용 예시 1:
 * ```kotlin
 * val bottomBarPadding = LocalBottomBarPadding.current
 * LazyColumn(contentPadding = PaddingValues(bottom = bottomBarPadding))
 * ```
 *
 * 사용 예시 2: Modifier의 패팅으루~
 * ```kotlin
 * val bottomBarPadding = LocalBottomBarPadding.current
 *
 * Column(
 *     modifier = Modifier
 *         .padding(bottom = bottomBarPadding)
 * ) {
 *
 *     // ... //
 *
 * }
 * ```
 *
 * 사용 예시 3: 스페이서로 쓰려며는 이렇게 할 수도?
 * ```kotlin
 * val bottomBarPadding = LocalBottomBarPadding.current
 * Column(){
 *
 *      //...//
 *
 *     Spacer(modifier = Modifier.height(bottomBarPadding))
 * }
 * ```
 *
 * 사용 예시 말고도 각자 화면에서 아래에 이 값을 사용해서 패딩이나 Spacer을 줘야 할 듯 해유..아니 이게 진짜
 * 요규사항이 바텀바가 화면 위에 떠있는 구조여야 바텀바 모서리 쪽을 만족시킬 수 있어요 ㅠㅠ
 */
val LocalBottomBarPadding = compositionLocalOf<Dp> { 0.dp }

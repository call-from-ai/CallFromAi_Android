package kr.co.call.designsystem.component.profileimage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.SubcomposeAsyncImage
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.distinctUntilChanged
import kr.co.call.designsystem.modifier.skeleton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 프로필 후보 이미지 가로 캐러셀 + 페이지 인디케이터
 *
 * ## 상태 계약
 * - 인디케이터는 [androidx.compose.foundation.pager.PagerState.currentPage]에 직결되어
 *   부모 [selectedImageId] 갱신 전에도 스와이프에 반응합니다.
 * - 페이지가 확정되면 [onImageSelected]를 호출합니다. 호출부가 [selectedImageId]를
 *   갱신해야 저장 시 최종 선택이 유지됩니다.
 * - 외부에서 [selectedImageId]가 바뀌면(성별 전환 등) 해당 페이지로 스크롤합니다.
 * - 목록 자체가 바뀌면 호출부에서 `key(images.map { it.id })` 등으로 이 컴포저블을
 *   재생성해 pager를 초기화하는 것을 권장합니다.
 */
@Composable
internal fun ProfileImageCarousel(
    images: List<ProfileImageOption>,
    selectedImageId: String?,
    onImageSelected: (ProfileImageOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(images.isNotEmpty()) {
        "ProfileImageCarousel에는 비어 있지 않은 이미지 목록이 필요합니다. 빈 목록은 UI에서 처리하세요."
    }

    val initialPage = remember(images) {
        images.indexOfFirst { it.id == selectedImageId }
            .takeIf { it >= 0 }
            ?: 0
    }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { images.size },
    )

    LaunchedEffect(selectedImageId, images) {
        val target = images.indexOfFirst { it.id == selectedImageId }
        if (target >= 0 && target != pagerState.currentPage && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(target)
        }
    }

    LaunchedEffect(pagerState, images) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                images.getOrNull(page)?.let(onImageSelected)
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(CenterImageHeight),
        ) {
            // 페이지 폭 = 중앙 카드 폭이 되도록 패딩 -> 현재 페이지가 화면 중앙
            val horizontalPadding = ((maxWidth - CenterImageWidth) / 2).coerceAtLeast(0.dp)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                pageSpacing = PageSpacing,
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue
                val offsetFraction = pageOffset.coerceIn(0f, 1f)

                val scaleX = lerp(
                    start = SIDE_SCALE_X,
                    stop = 1f,
                    fraction = 1f - offsetFraction,
                )
                val scaleY = lerp(
                    start = SIDE_SCALE_Y,
                    stop = 1f,
                    fraction = 1f - offsetFraction,
                )
                val dimAlpha = lerp(
                    start = SIDE_DIM_ALPHA,
                    stop = 0f,
                    fraction = 1f - offsetFraction,
                )

                val option = images[page]
                // 슬롯은 fillMaxWidth, 카드만 Center -> 페이지 내 start 정렬로 인한 왼쪽 쏠림 방지
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                this.scaleX = scaleX
                                this.scaleY = scaleY
                            }
                            .size(width = CenterImageWidth, height = CenterImageHeight)
                            .clip(RoundedCornerShape(CardCornerRadius))
                            .background(CallTheme.colors.gray100),
                    ) {
                        ProfileCandidateImage(imageUrl = option.imageUrl)
                        if (dimAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = dimAlpha)),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(CarouselIndicatorGap))

        ProfileImagePageIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
        )
    }
}

@Composable
private fun ProfileCandidateImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = imageUrl.takeIf { it.isNotBlank() },
        contentDescription = "프로필 후보 사진",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .skeleton(isLoading = true),
            )
        },
    )
}

@Composable
internal fun ProfileImageCarouselLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CenterImageHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = CenterImageWidth, height = CenterImageHeight)
                .clip(RoundedCornerShape(CardCornerRadius))
                .skeleton(isLoading = true),
        )
    }
}

// 하단 dot indicator
@Composable
private fun ProfileImagePageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 0) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 6.dp else 5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            CallTheme.colors.mainVariant1
                        } else {
                            CallTheme.colors.gray200
                        },
                    ),
            )
        }
    }
}

/** 중앙(선택) 카드 사이즈 */
internal val CenterImageWidth = 225.dp
internal val CenterImageHeight = 249.dp

/** 양옆 카드 배율 */
internal const val SIDE_SCALE_X = 160f / 225f
internal const val SIDE_SCALE_Y = 179f / 249f

/** 비선택 카드 dim 알파 */
private const val SIDE_DIM_ALPHA = 0.4f

private val CardCornerRadius = 20.dp
private val PageSpacing = (-24).dp
private val CarouselIndicatorGap = 17.dp

/**
 * Preview : 캐러셀 + 인디케이터
 */
@Preview(
    name = "Carousel_Interactive",
    showBackground = true,
)
@Composable
private fun ProfileImageCarouselPreview() {
    CallFromAiTheme {
        val images = ProfileImagePickerPreviewData.femaleImages
        var selectedImageId by remember { mutableStateOf(images.first().id) }

        ProfileImageCarousel(
            images = images,
            selectedImageId = selectedImageId,
            onImageSelected = { selectedImageId = it.id },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        )
    }
}
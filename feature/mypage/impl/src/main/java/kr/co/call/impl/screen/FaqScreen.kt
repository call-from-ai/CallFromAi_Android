package kr.co.call.impl.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.mypage.FaqCategory
import kr.co.call.domain.model.mypage.FaqItem
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.viewmodel.FaqViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    viewModel: FaqViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.collectAsState()
    var selectedCategory by remember { mutableStateOf(FaqCategory.CHARACTER) }
    var expandedQuestions by rememberSaveable(selectedCategory) { mutableStateOf(setOf<String>()) }

    FaqScreenContent(
        selectedCategory = selectedCategory,
        items = state.itemsByCategory[selectedCategory].orEmpty(),
        expandedQuestions = expandedQuestions,
        loadStatus = state.loadStatus,
        onSelectCategory = { selectedCategory = it },
        onToggleQuestion = { question ->
            expandedQuestions = if (question in expandedQuestions) {
                expandedQuestions - question
            } else {
                expandedQuestions + question
            }
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun FaqScreenContent(
    selectedCategory: FaqCategory,
    items: List<FaqItem>,
    expandedQuestions: Set<String>,
    loadStatus: LoadStatus,
    onSelectCategory: (FaqCategory) -> Unit,
    onToggleQuestion: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding(),
    ) {
        // 상단 앱바
        CommonTopAppBar(title = "자주 하는 질문", onBackClick = onBackClick)

        // 카테고리
        FaqCategoryTabs(
            selected = selectedCategory,
            onSelect = onSelectCategory,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // QnA
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = if (items.isEmpty()) Alignment.Center else Alignment.TopStart,
        ) {
            when {
                loadStatus == LoadStatus.Loading -> {
                    Text(
                        text = "불러오는 중...",
                        style = CallTheme.typography.bodyMedium,
                        color = CallTheme.colors.gray400,
                    )
                }
                loadStatus is LoadStatus.Error -> {
                    Text(
                        text = loadStatus.message,
                        style = CallTheme.typography.bodyMedium,
                        color = CallTheme.colors.gray400,
                    )
                }
                items.isEmpty() -> {
                    Text(
                        text = "등록된 질문이 없습니다.",
                        style = CallTheme.typography.bodyMedium,
                        color = CallTheme.colors.gray400,
                    )
                }
                else ->
                Column {
                    items.forEachIndexed { index, item ->

                        // QnA 아이템
                        FaqQnaItem(
                            item = item,
                            expanded = item.question in expandedQuestions,
                            onToggle = { onToggleQuestion(item.question) },
                        )
                        // 구분선
                        HorizontalDivider(color = CallTheme.colors.gray100, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

// 카테고리 버튼 컴포넌트
@Composable
private fun FaqCategoryTabs(
    selected: FaqCategory,
    onSelect: (FaqCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 18.dp, bottom = 29.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FaqCategory.entries.forEach { category ->
            val isSelected = category == selected
            val shape = RoundedCornerShape(10.dp)

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) CallTheme.colors.main else CallTheme.colors.white)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) CallTheme.colors.main else CallTheme.colors.gray200,
                        shape = shape,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onSelect(category) },
                    )
                    .padding(horizontal = 15.dp, vertical = 10.dp),
            ) {
                Text(
                    text = category.label,
                    style = CallTheme.typography.bodyMedium,
                    color =  if (isSelected) CallTheme.colors.white else CallTheme.colors.black,
                )
            }
        }
    }
}

// QnA 아이템
@Composable
private fun FaqQnaItem(
    item: FaqItem,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 질문 행
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onToggle,
                )
                .padding(horizontal = 31.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Q",
                style = CallTheme.typography.titleMediumBold,
                color = CallTheme.colors.black,
            )
            Text(
                text = item.question,
                style = CallTheme.typography.bodySmall,
                color = CallTheme.colors.black,
                modifier = Modifier.weight(1f),
            )
        }

        // 답변 행 (토글)
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CallTheme.colors.gray100)
                    .padding(vertical = 15.dp, horizontal = 31.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "A",
                    style = CallTheme.typography.titleMediumBold,
                    color = CallTheme.colors.black,
                )
                Text(
                    text = item.answer,
                    style = CallTheme.typography.caption,
                    color = CallTheme.colors.black,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "기본 (모두 닫힘)")
@Composable
private fun FaqScreenPreview() {
    CallFromAiTheme {
        FaqScreenContent(
            selectedCategory = FaqCategory.CHARACTER,
            items = listOf(
                FaqItem(
                    "AI 캐릭터는 여러 명 만들 수 있나요?",
                    "새로운 캐릭터를 만든 후 24시간이 지난 후, 최초 1회 변경은 무료로 가능하며, 추가 캐릭터 생성 원할 시 캐릭터 변경권을 따로 구매하여 변경하실 수 있습니다."
                ),
                FaqItem(
                    "채팅방에서 나가면 캐릭터도 함께 삭제되나요?",
                    "채팅방에서 나가도 캐릭터는 삭제되지 않습니다. 캐릭터 삭제는 마이페이지>캐릭터 관리>캐릭터 영구 삭제하기 버튼을 통해 가능합니다."
                ),
                FaqItem(
                    "중간에 캐릭터의 이름이나 설정을 변경할 수도 있나요?",
                    "네, 마이페이지>이상형 정보 수정에서 이름, 프로필 이미지, 성별, 직업 등의 정보를 변경할 수 있습니다."
                ),
                FaqItem(
                    "새로운 캐릭터를 만들려면 어떻게 하나요?",
                    "마이페이지>캐릭터 관리>캐릭터 추가하기 버튼을 눌러 새로운 캐릭터를 생성할 수 있습니다."
                ),
            ),
            expandedQuestions = emptySet(),
            loadStatus = LoadStatus.Idle,
            onSelectCategory = {},
            onToggleQuestion = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, name = "질문 펼침")
@Composable
private fun FaqScreenExpandedPreview() {
    val items = listOf(
        FaqItem(
            "AI 캐릭터는 여러 명 만들 수 있나요?",
            "새로운 캐릭터를 만든 후 24시간이 지난 후, 최초 1회 변경은 무료로 가능하며, 추가 캐릭터 생성 원할 시 캐릭터 변경권을 따로 구매하여 변경하실 수 있습니다."
        ),
        FaqItem(
            "채팅방에서 나가면 캐릭터도 함께 삭제되나요?",
            "채팅방에서 나가도 캐릭터는 삭제되지 않습니다. 캐릭터 삭제는 마이페이지>캐릭터 관리>캐릭터 영구 삭제하기 버튼을 통해 가능합니다."
        ),
        FaqItem(
            "중간에 캐릭터의 이름이나 설정을 변경할 수도 있나요?",
            "네, 마이페이지>이상형 정보 수정에서 이름, 프로필 이미지, 성별, 직업 등의 정보를 변경할 수 있습니다."
        ),
        FaqItem(
            "새로운 캐릭터를 만들려면 어떻게 하나요?",
            "마이페이지>캐릭터 관리>캐릭터 추가하기 버튼을 눌러 새로운 캐릭터를 생성할 수 있습니다."
        ),
    )
    CallFromAiTheme {
        FaqScreenContent(
            selectedCategory = FaqCategory.CHARACTER,
            items = items,
            expandedQuestions = setOf(items.first().question),
            loadStatus = LoadStatus.Idle,
            onSelectCategory = {},
            onToggleQuestion = {},
            onBackClick = {},
        )
    }
}


@Preview(showBackground = true, name = "빈 카테고리")
@Composable
private fun FaqScreenEmptyPreview() {
    CallFromAiTheme {
        FaqScreenContent(
            selectedCategory = FaqCategory.PLAN_PAYMENT,
            items = emptyList(),
            expandedQuestions = emptySet(),
            loadStatus = LoadStatus.Idle,
            onSelectCategory = {},
            onToggleQuestion = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, name = "에러 상태")
@Composable
private fun FaqScreenErrorPreview() {
    CallFromAiTheme {
        FaqScreenContent(
            selectedCategory = FaqCategory.CHARACTER,
            items = emptyList(),
            expandedQuestions = emptySet(),
            loadStatus = LoadStatus.Error("네트워크 오류가 발생했습니다."),
            onSelectCategory = {},
            onToggleQuestion = {},
            onBackClick = {},
        )
    }
}
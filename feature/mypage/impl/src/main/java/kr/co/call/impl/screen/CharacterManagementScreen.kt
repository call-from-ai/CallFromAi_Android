package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.component.popup.OneButtonPopup
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.impl.component.CharacterCard
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.viewmodel.CharacterManagementIntent
import kr.co.call.impl.viewmodel.CharacterManagementSideEffect
import kr.co.call.impl.viewmodel.CharacterManagementState
import kr.co.call.impl.viewmodel.CharacterManagementViewModel
import kr.co.call.impl.viewmodel.MyPageState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CharacterManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: CharacterManagementViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    navigateToAddCharacter: () -> Unit,
) {
    val state by viewModel.collectAsState()

    var chatHistoryTarget by remember { mutableStateOf<AiCharacter?>(null) }
    var deleteTarget by remember { mutableStateOf<AiCharacter?>(null) }
    var showMainDeleteBlocked by remember { mutableStateOf(false) }
    var showAddBlocked by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CharacterManagementSideEffect.ShowChatHistorySummary -> chatHistoryTarget = sideEffect.aiCharacter
            is CharacterManagementSideEffect.ShowDeleteConfirmDialog -> deleteTarget = sideEffect.aiCharacter
            is CharacterManagementSideEffect.ShowMainCharacterDeleteBlocked -> showMainDeleteBlocked = true
            is CharacterManagementSideEffect.ShowAddCharacterBlocked -> showAddBlocked = true
            is CharacterManagementSideEffect.NavigateToAddCharacter -> navigateToAddCharacter()
        }
    }

    CharacterManagementScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )

    chatHistoryTarget?.let { character ->
        OneButtonPopup(
            label = "채팅 기록 보기",
            description = buildAnnotatedString { append(character.summary) },
            buttonText = "확인",
            onButtonClick = { chatHistoryTarget = null },
            onDismissRequest = { chatHistoryTarget = null },
        )
    }

    deleteTarget?.let { character ->
        TwoButtonPopup(
            label = "캐릭터 영구 삭제",
            title = "${character.name}와 헤어지시겠습니까?",
            description = buildAnnotatedString { append("데이터 복구 및 재사용 불가합니다.") },
            positiveText = "삭제",
            negativeText = "취소",
            onPositiveClick = {
                viewModel.handleIntent(CharacterManagementIntent.ConfirmDeleteCharacter(character))
                deleteTarget = null
            },
            onNegativeClick = { deleteTarget = null },
            onDismissRequest = { deleteTarget = null },
        )
    }

    if (showMainDeleteBlocked) {
        OneButtonPopup(
            label = "캐릭터 영구 삭제",
            title = "현재 메인으로 선택된\n연인은 삭제가 불가능합니다.",
            buttonText = "확인",
            onButtonClick = { showMainDeleteBlocked = false },
            onDismissRequest = { showMainDeleteBlocked = false },
        )
    }

    if (showAddBlocked) {
        OneButtonPopup(
            label = "캐릭터 추가하기",
            title = "아직 캐릭터를\n추가할 수 없어요",
            description = buildAnnotatedString {
                append("새로운 캐릭터를 만든 후 24시간이 지나면\n또 다른 캐릭터를 만들 수 있어요")
            },
            buttonText = "확인",
            onButtonClick = { showAddBlocked = false },
            onDismissRequest = { showAddBlocked = false },
        )
    }
}

@Composable
private fun CharacterManagementScreenContent(
    state: CharacterManagementState,
    onIntent: (CharacterManagementIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().background(CallTheme.colors.background),
    ) {
        // 상단 앱바
        CommonTopAppBar(title = "캐릭터 관리", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 15.dp),
        ) {
            // 안내 문구
            Text(
                text = "생성 가능한 캐릭터 수는 최대 5개입니다.",
                style = CallTheme.typography.caption,
                color = CallTheme.colors.gray400,
            )

            Spacer(modifier = Modifier.height(21.dp))

            // 캐릭터 아이템 목록
            state.aiCharacters.forEach { character ->
                CharacterCard(
                    aiCharacter = character,
                    onChatHistoryClick = { onIntent(CharacterManagementIntent.ClickChatHistory(character)) },
                    onDeleteClick = { onIntent(CharacterManagementIntent.ClickDeleteCharacter(character)) },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        // 캐릭터 추가 버튼
        SecondaryButton(
            text = "캐릭터 추가하기",
            onClick = { onIntent(CharacterManagementIntent.ClickAddCharacter) },
            containerColor = CallTheme.colors.mainVariant4,
            contentColor = CallTheme.colors.white,
            modifier = Modifier.padding(bottom = 24.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

// preview용 state
private val previewState = CharacterManagementState(
    aiCharacters = listOf(
        AiCharacter("1", "김민준", "", true, "2025.05.26", "35일 째", "오늘 21:42", "아이스크림을 커피보다 좋아해요."),
        AiCharacter("2", "박동휘", "", false, "2025.06.19", "11일 째", "6/29 12:30", "운동을 좋아해요."),
        AiCharacter("3", "정유나", "", false, "2025.06.25", "5일 째", "6/28 09:04", "독서를 좋아해요."),
    ),
)

@Preview(showBackground = true)
@Composable
private fun CharacterManagementScreenPreview() {
    CallFromAiTheme {
        CharacterManagementScreenContent(
            state = previewState,
            onIntent = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatHistoryPopupPreview() {
    CallFromAiTheme {
        CharacterManagementScreenContent(
            state = previewState,
            onIntent = {},
            onBackClick = {},
        )
        OneButtonPopup(
            label = "채팅 기록 보기",
            description = buildAnnotatedString { append("아이스티를 커피보다 좋아하며, 늦은 저녁 시간에 대화하는 것을 선호해요. 디자인과 여행 이야기에 관심이 많고, 반존대를 편안하게 느껴요. ") },
            buttonText = "확인",
            onButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteConfirmPopupPreview() {
    CallFromAiTheme {
        CharacterManagementScreenContent(
            state = previewState,
            onIntent = {},
            onBackClick = {},
        )
        TwoButtonPopup(
            label = "캐릭터 영구 삭제",
            title = "동휘와 헤어지시겠습니까?",
            description = buildAnnotatedString { append("데이터 복구 및 재사용 불가합니다.") },
            positiveText = "삭제",
            negativeText = "취소",
            onPositiveClick = {},
            onNegativeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainDeleteBlockedPopupPreview() {
    CallFromAiTheme {
        CharacterManagementScreenContent(
            state = previewState,
            onIntent = {},
            onBackClick = {},
        )
        OneButtonPopup(
            label = "캐릭터 영구 삭제",
            title = "현재 메인으로 선택된\n연인은 삭제가 불가능합니다.",
            buttonText = "확인",
            onButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBlockedPopupPreview() {
    CallFromAiTheme {
        CharacterManagementScreenContent(
            state = previewState,
            onIntent = {},
            onBackClick = {},
        )
        OneButtonPopup(
            label = "캐릭터 추가하기",
            title = "아직 캐릭터를\n추가할 수 없어요",
            description = buildAnnotatedString {
                append("새로운 캐릭터를 만든 후 24시간이 지나면\n또 다른 캐릭터를 만들 수 있어요")
            },
            buttonText = "확인",
            onButtonClick = {},
        )
    }
}
package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class CharacterManagementViewModel @Inject constructor(
    private val aiCharacterRepository: AICharacterRepository,
) : ViewModel(), ContainerHost<CharacterManagementState, CharacterManagementSideEffect> {

    override val container: Container<CharacterManagementState, CharacterManagementSideEffect> = container(
        initialState = CharacterManagementState(),
    ) {
        loadCharacters()
    }

    fun handleIntent(userIntent: CharacterManagementIntent) {
        when (userIntent) {
            is CharacterManagementIntent.ClickChatHistory -> showChatHistory(userIntent.aiCharacter)
            is CharacterManagementIntent.DismissChatHistory -> dismissChatHistory()
            is CharacterManagementIntent.ClickEditCharacter -> navigateToEdit(userIntent.aiCharacter)
            is CharacterManagementIntent.ClickDeleteCharacter -> handleDeleteClick(userIntent.aiCharacter)
            is CharacterManagementIntent.ConfirmDeleteCharacter -> deleteCharacter(userIntent.aiCharacter.id)
            is CharacterManagementIntent.ClickAddCharacter -> checkAddCharacter()
        }
    }

    private fun loadCharacters() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }

        aiCharacterRepository.getCharacters()
            .onSuccess { characters ->
                reduce { state.copy(aiCharacters = characters, loadStatus = LoadStatus.Idle) }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                reduce { state.copy(loadStatus = LoadStatus.Error(e.message ?: "캐릭터 목록 불러오기 실패")) }
            }
    }

    private fun showChatHistory(aiCharacter: AiCharacter) = intent {
        val characterId = aiCharacter.id.toLongOrNull()
        if (characterId == null) {
            postSideEffect(
                CharacterManagementSideEffect.ShowMessage("잘못된 캐릭터입니다."),
            )
            return@intent
        }

        // 연타 방지: 이미 로딩 중이면 무시
        if (state.chatHistoryUi is ChatHistoryUi.Loading) return@intent

        // API 전에 팝업을 먼저 연다
        reduce { state.copy(chatHistoryUi = ChatHistoryUi.Loading) }

        aiCharacterRepository.getChatSummary(characterId)
            .onSuccess { summary ->
                reduce {
                    // 로딩 중에 닫았으면 결과로 다시 열지 않음
                    if (state.chatHistoryUi !is ChatHistoryUi.Loading) {
                        state
                    } else {
                        state.copy(
                            chatHistoryUi = ChatHistoryUi.Ready(
                                summary = summary.ifBlank { FALLBACK_EMPTY_CHAT_SUMMARY },
                            ),
                        )
                    }
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce {
                    if (state.chatHistoryUi !is ChatHistoryUi.Loading) {
                        state
                    } else {
                        state.copy(
                            chatHistoryUi = ChatHistoryUi.Ready(FALLBACK_UNAVAILABLE_CHAT_SUMMARY),
                        )
                    }
                }
            }
    }

    private fun dismissChatHistory() = intent {
        reduce { state.copy(chatHistoryUi = ChatHistoryUi.Hidden) }
    }

    private companion object {
        const val FALLBACK_EMPTY_CHAT_SUMMARY = "아직 대화 요약이 없어요."
        const val FALLBACK_UNAVAILABLE_CHAT_SUMMARY =
            "대화 요약을 불러오지 못했어요.\n잠시 후 다시 시도해 주세요."
    }

    private fun navigateToEdit(aiCharacter: AiCharacter) = intent {
        val id = aiCharacter.id.toLongOrNull()
        if (id == null) return@intent
        postSideEffect(CharacterManagementSideEffect.NavigateToEditCharacter(id))
    }

    private fun handleDeleteClick(aiCharacter: AiCharacter) = intent {
        if (aiCharacter.isMain) {
            postSideEffect(CharacterManagementSideEffect.ShowMainCharacterDeleteBlocked)
        } else {
            postSideEffect(CharacterManagementSideEffect.ShowDeleteConfirmDialog(aiCharacter))
        }
    }

    private fun deleteCharacter(characterId: String) = intent {
        val targetCharacter = state.aiCharacters.find { it.id == characterId }

        if (targetCharacter?.isMain == true) {
            postSideEffect(CharacterManagementSideEffect.ShowMainCharacterDeleteBlocked)
            return@intent
        }

        aiCharacterRepository.deleteCharacter(characterId)
            .onSuccess {
                reduce { state.copy(aiCharacters = state.aiCharacters.filterNot { it.id == characterId }) }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                // TODO 삭제 실패 시 처리
            }
    }

    private fun checkAddCharacter() = intent {
        aiCharacterRepository.canAddCharacter()
            .onSuccess { canAdd ->
                if (canAdd) {
                    postSideEffect(CharacterManagementSideEffect.NavigateToAddCharacter)
                } else {
                    postSideEffect(CharacterManagementSideEffect.ShowAddCharacterBlocked)
                }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                postSideEffect(CharacterManagementSideEffect.ShowAddCharacterBlocked)
            }
    }
}
package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CharacterManagementViewModel @Inject constructor(
    private val aiCharacterRepository: AICharacterRepository,
) : ViewModel(), ContainerHost<CharacterManagementState, CharacterManagementSideEffect> {

    override val container: Container<CharacterManagementState, CharacterManagementSideEffect> = container(
        initialState = CharacterManagementState()
    ) {
        loadCharacters()
    }

    fun handleIntent(userIntent: CharacterManagementIntent) {
        when (userIntent) {
            is CharacterManagementIntent.ClickChatHistory -> showChatHistory(userIntent.aiCharacter)
            is CharacterManagementIntent.ClickDeleteCharacter -> handleDeleteClick(userIntent.aiCharacter)
            is CharacterManagementIntent.ConfirmDeleteCharacter -> deleteCharacter(userIntent.aiCharacter.id)
            is CharacterManagementIntent.ClickAddCharacter -> checkAddCharacter()
        }
    }

    private fun loadCharacters() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        runCatching { aiCharacterRepository.getCharacters() }
            .onSuccess { characters ->
                reduce { state.copy(aiCharacters = characters, loadStatus = LoadStatus.Idle) }
            }
            .onFailure { e ->
                reduce { state.copy(loadStatus = LoadStatus.Error(e.message ?: "캐릭터 목록 불러오기 실패")) }
            }
    }

    private fun showChatHistory(aiCharacter: AiCharacter) = intent {
        postSideEffect(CharacterManagementSideEffect.ShowChatHistorySummary(aiCharacter))
    }

    private fun handleDeleteClick(aiCharacter: AiCharacter) = intent {
        if (aiCharacter.isMain) {
            postSideEffect(CharacterManagementSideEffect.ShowMainCharacterDeleteBlocked)
        } else {
            postSideEffect(CharacterManagementSideEffect.ShowDeleteConfirmDialog(aiCharacter))
        }
    }

    private fun deleteCharacter(characterId: String) = intent {
        aiCharacterRepository.deleteCharacter(characterId)
        reduce { state.copy(aiCharacters = state.aiCharacters.filterNot { it.id == characterId }) }
    }

    private fun checkAddCharacter() = intent {
        if (aiCharacterRepository.canAddCharacter()) {
            postSideEffect(CharacterManagementSideEffect.NavigateToAddCharacter)
        } else {
            postSideEffect(CharacterManagementSideEffect.ShowAddCharacterBlocked)
        }
    }
}
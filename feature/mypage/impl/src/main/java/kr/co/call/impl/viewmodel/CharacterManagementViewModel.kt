package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.CancellationException
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
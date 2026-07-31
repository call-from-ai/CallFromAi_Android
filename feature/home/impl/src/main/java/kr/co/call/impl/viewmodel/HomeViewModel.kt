package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.exception.CharacterChangeUnavailableException
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.state.HomeDialogState
import kr.co.call.impl.viewmodel.state.HomeState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) :
    ViewModel(),
    ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(
        initialState = HomeState(),
    )

    init {
        loadHome()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.History -> handleHistoryIntent(intent)
            is HomeIntent.Call -> handleCallIntent(intent)
            is HomeIntent.Character -> handleCharacterIntent(intent)
            HomeIntent.DismissDialog -> dismissDialog()
        }
    }

    private fun handleHistoryIntent(intent: HomeIntent.History) {
        when (intent) {
            is HomeIntent.History.SelectTab -> selectHistoryTab(intent.tab)
            HomeIntent.History.ClickNotificationShortcut -> {
                selectHistoryTab(HomeHistoryTab.NOTIFICATION)
            }
            is HomeIntent.History.ClickRecord -> navigateToCallRecord(intent.callId)
        }
    }

    private fun handleCallIntent(intent: HomeIntent.Call) {
        when (intent) {
            HomeIntent.Call.ClickMain -> showMainCharacterCallConfirmation()
            is HomeIntent.Call.ClickNotification -> {
                showCallDialog(characterName = intent.characterName)
            }
            HomeIntent.Call.Confirm -> confirmCall()
        }
    }

    private fun handleCharacterIntent(intent: HomeIntent.Character) {
        when (intent) {
            HomeIntent.Character.ClickChange -> showCharacterSelectionDialog()
            is HomeIntent.Character.Select -> {
                showCharacterChangeConfirmation(
                    characterId = intent.characterId,
                    characterName = intent.characterName,
                )
            }
            HomeIntent.Character.ConfirmChange -> confirmCharacterChange()
            HomeIntent.Character.ClickAdd -> navigateToCharacterOnboarding()
        }
    }

    // 메인 캐릭터 통화 확인 팝업 표시
    private fun showMainCharacterCallConfirmation() = intent {
        val mainCharacter = state.characters.firstOrNull { character -> character.isMain }
            ?: return@intent

        reduce {
            state.copy(
                dialogState = HomeDialogState.CallConfirmation(
                    characterId = mainCharacter.id,
                    characterName = mainCharacter.name,
                ),
            )
        }
    }

    // 통화 대상에 맞는 팝업 표시
    private fun showCallDialog(characterName: String) = intent {
        val mainCharacter = state.characters.firstOrNull { character -> character.isMain }
        val dialogState = if (mainCharacter?.name == characterName) {
            HomeDialogState.CallConfirmation(
                characterId = mainCharacter.id,
                characterName = characterName,
            )
        } else {
            HomeDialogState.NonMainCharacterCall(characterName = characterName)
        }

        reduce {
            state.copy(dialogState = dialogState)
        }
    }

    // 통화 연결 화면으로 이동
    private fun confirmCall() = intent {
        val confirmation = state.dialogState as? HomeDialogState.CallConfirmation
            ?: return@intent

        try {
            homeRepository.startCall(
                characterId = confirmation.characterId,
            ).getOrThrow()

            reduce {
                state.copy(dialogState = null)
            }
            postSideEffect(
                HomeSideEffect.NavigateToCall(
                    characterId = confirmation.characterId,
                ),
            )
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            postSideEffect(
                HomeSideEffect.ShowMessage(
                    message = throwable.message ?: "통화를 시작하지 못했습니다.",
                ),
            )
        }
    }

    // 캐릭터 선택 팝업 표시
    private fun showCharacterSelectionDialog() = intent {
        reduce {
            state.copy(dialogState = HomeDialogState.CharacterSelection)
        }
    }

    // 캐릭터 선택 확인/취소
    private fun showCharacterChangeConfirmation(
        characterId: Long,
        characterName: String,
    ) = intent {
        reduce {
            state.copy(
                dialogState = HomeDialogState.CharacterChangeConfirmation(
                    characterId = characterId,
                    characterName = characterName,
                ),
            )
        }
    }

    // 캐릭터 변경 확인
    private fun confirmCharacterChange() = intent {
        val confirmation = state.dialogState as? HomeDialogState.CharacterChangeConfirmation
            ?: return@intent

        try {
            homeRepository.activateCharacter(
                characterId = confirmation.characterId,
            ).getOrThrow()
            val characters = homeRepository.getCharacters().getOrThrow()

            reduce {
                state.copy(
                    characters = characters,
                    dialogState = null,
                )
            }
            postSideEffect(
                HomeSideEffect.ShowMessage(
                    message = "${confirmation.characterName}로 메인 연인을 교체했습니다.",
                ),
            )
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: CharacterChangeUnavailableException) {
            reduce {
                state.copy(
                    dialogState = HomeDialogState.CharacterChangeUnavailable,
                )
            }
        } catch (throwable: Throwable) {
            postSideEffect(
                HomeSideEffect.ShowMessage(
                    message = throwable.message ?: "메인 연인을 변경하지 못했습니다.",
                ),
            )
        }
    }


    // 온보딩으로 이동
    private fun navigateToCharacterOnboarding() = intent {
        reduce {
            state.copy(dialogState = null)
        }
        postSideEffect(HomeSideEffect.NavigateToCharacterOnboarding)
    }

    private fun navigateToCallRecord(callId: Long) = intent {
        postSideEffect(HomeSideEffect.NavigateToCallRecord(callId = callId))
    }

    private fun dismissDialog() = intent {
        reduce {
            state.copy(dialogState = null)
        }
    }


    // 기록 탭 선택
    private fun selectHistoryTab(
        tab: HomeHistoryTab,
    ) = intent {
        reduce {
            state.copy(
                selectedHistoryTab = tab,
            )
        }
    }

    private fun loadHome() = intent {
        reduce {
            state.copy(loadStatus = LoadStatus.Loading)
        }

        try {
            val summary = homeRepository.getSummary().getOrThrow()
            val callHistories = homeRepository.getCallHistories().getOrThrow()
            val characters = homeRepository.getCharacters().getOrThrow()

            reduce {
                state.copy(
                    summary = summary,
                    callHistories = callHistories,
                    characters = characters,
                    loadStatus = LoadStatus.Idle,
                )
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            reduce {
                state.copy(
                    loadStatus = LoadStatus.Error(
                        message = throwable.message ?: "홈 정보를 불러오지 못했습니다.",
                    ),
                )
            }
        }
    }
}

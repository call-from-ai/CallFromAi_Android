package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.exception.AppException
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.state.HomeDialogState
import kr.co.call.impl.viewmodel.state.HomeState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
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
        refresh()
    }

    private fun refresh() {
        loadSummary()
        loadCallHistories()
        loadCharacters()
        loadNotifications()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.History -> handleHistoryIntent(intent)
            is HomeIntent.Call -> handleCallIntent(intent)
            is HomeIntent.Character -> handleCharacterIntent(intent)
            HomeIntent.DismissDialog -> dismissDialog()
            HomeIntent.OnResume -> refresh()
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
                showCallDialog(
                    characterId = intent.characterId,
                    characterName = intent.characterName,
                )
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
    private fun showCallDialog(
        characterId: Long,
        characterName: String,
    ) = intent {
        val targetCharacter = state.characters.firstOrNull { character -> character.id == characterId }

        val dialogState = if (targetCharacter?.isMain == true) {
            HomeDialogState.CallConfirmation(
                characterId = characterId,
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

        reduce {
            state.copy(dialogState = null)
        }
        postSideEffect(
            HomeSideEffect.NavigateToCall(
                characterId = confirmation.characterId,
                characterName = confirmation.characterName,
                characterImageUrl = state.characters
                    .firstOrNull { character -> character.id == confirmation.characterId }
                    ?.imageUrl,
            ),
        )
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
        } catch (exception: AppException.Business) {
            if (exception.code == CHARACTER_CHANGE_UNAVAILABLE_CODE) {
                reduce {
                    state.copy(
                        // 캐릭터 변경 후 3일 지나야 변경 가능함 표시
                        dialogState = HomeDialogState.CharacterChangeUnavailable,
                    )
                }
            } else {
                postSideEffect(
                    HomeSideEffect.ShowMessage(
                        message = exception.message ?: "메인 연인을 변경하지 못했습니다.",
                    ),
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

    // 상단 요약 정보 조회
    private fun loadSummary() = intent {
        homeRepository.getSummary()
            .onSuccess { summary ->
                reduce { state.copy(summary = summary) }
            }
            .onFailure {
                postSideEffect(HomeSideEffect.ShowMessage("정보를 불러오지 못했습니다."))
            }
    }

    // 통화 기록 조회
    private fun loadCallHistories() = intent {
        homeRepository.getCallHistories()
            .onSuccess { callHistories ->
                reduce { state.copy(callHistories = callHistories) }
            }
            .onFailure {
                postSideEffect(HomeSideEffect.ShowMessage("통화 기록을 불러오지 못했습니다."))
            }
    }

    // 캐릭터 목록 조회
    private fun loadCharacters() = intent {
        homeRepository.getCharacters()
            .onSuccess { characters ->
                reduce { state.copy(characters = characters) }
            }
            .onFailure {
                postSideEffect(HomeSideEffect.ShowMessage("캐릭터 목록을 불러오지 못했습니다."))
            }
    }

    // 지난 알림 목록 조회
    private fun loadNotifications() = intent {
        // 안 읽은 알림 읽음처리 후 조회
        homeRepository.readAllNotifications()
            .onFailure { Timber.e(it, "알림 읽음 처리 실패") }
        homeRepository.getNotifications()
            .onSuccess { notifications ->
                reduce {
                    state.copy(
                        notifications = notifications,
                        hasUnreadNotification = notifications.any { notification -> !notification.isRead },
                    )
                }
            }
            .onFailure {
                postSideEffect(HomeSideEffect.ShowMessage("알림을 불러오지 못했습니다."))
            }
    }

    private companion object {
        const val CHARACTER_CHANGE_UNAVAILABLE_CODE = "CHAR008"
    }
}

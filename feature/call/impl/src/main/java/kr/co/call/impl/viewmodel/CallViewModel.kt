package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.call.domain.repository.CallSessionRepository
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

// 통화 종료 시 2.5초 후에 홈으로 이동 처리를 위한 변수
private const val CALL_ENDED_HOME_DELAY_MILLIS = 2500L

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callSessionRepository: CallSessionRepository,
) :
    ViewModel(),
    ContainerHost<CallState, CallSideEffect> {

    override val container: Container<CallState, CallSideEffect> = container(
        initialState = CallState(),
    )

    init {
        observeSessionState()
    }

    fun handleIntent(intent: CallIntent) {
        when (intent) {
            is CallIntent.Initialize -> initialize(
                characterId = intent.characterId,
                characterName = intent.characterName,
            )
            is CallIntent.MicrophonePermissionResult -> {
                handleMicrophonePermissionResult(intent.isGranted)
            }
            CallIntent.EndCall -> finishCall()
            CallIntent.ToggleMicrophone -> toggleMicrophone()
            CallIntent.ToggleSpeaker -> toggleSpeaker()
        }
    }

    private fun initialize(
        characterId: Long,
        characterName: String,
    ) = intent {
        if (state.characterId == characterId && state.character.name == characterName) {
            return@intent
        }

        reduce {
            state.copy(
                characterId = characterId,
                character = state.character.copy(name = characterName),
            )
        }
    }

    private fun finishCall() = intent {
        reduce {
            state.copy(phase = CallPhase.ENDING)
        }
        runCatching {
            callSessionRepository.endSession()
        }.onFailure {
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
        }.onSuccess {
            reduce {
                state.copy(phase = CallPhase.ENDED)
            }
        }
        delay(CALL_ENDED_HOME_DELAY_MILLIS)
        postSideEffect(CallSideEffect.FinishCall)
    }

    private fun toggleMicrophone() = intent {
        callSessionRepository.setMicrophoneEnabled(
            enabled = !state.sessionState.isMicrophoneEnabled,
        )
    }

    private fun toggleSpeaker() = intent {
        callSessionRepository.setSpeakerEnabled(
            enabled = !state.sessionState.isSpeakerEnabled,
        )
    }

    private fun startSession() = intent {
        if (!callSessionRepository.startSession()) {
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
            delay(CALL_ENDED_HOME_DELAY_MILLIS)
            postSideEffect(CallSideEffect.FinishCall)
        }
    }

    // 마이크 권한 요청 결과에 따른 처리
    private fun handleMicrophonePermissionResult(isGranted: Boolean) {
        // 권한 승인이 되면 통화 세션 시작
        if (isGranted) {
            startSession()
            return
        }

        intent {
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
            delay(CALL_ENDED_HOME_DELAY_MILLIS)
            postSideEffect(CallSideEffect.FinishCall)
        }
    }

    private fun observeSessionState() {
        viewModelScope.launch {
            callSessionRepository.sessionState.collectLatest { sessionState ->
                intent {
                    reduce {
                        state.copy(sessionState = sessionState)
                    }
                }
            }
        }
    }
}

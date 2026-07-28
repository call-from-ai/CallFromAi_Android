package kr.co.call.impl.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.co.call.domain.repository.CallSessionRepository
import kr.co.call.impl.viewmodel.model.CallDirection
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

// 통화 종료 시 2.5초 후에 홈으로 이동 처리를 위한 변수
private const val CALL_ENDED_HOME_DELAY_MILLIS = 2500L
private const val DURATION_UPDATE_INTERVAL_MILLIS = 1000L

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callSessionRepository: CallSessionRepository,
) :
    ViewModel(),
    ContainerHost<CallState, CallSideEffect> {

    override val container: Container<CallState, CallSideEffect> = container(
        initialState = CallState(),
    )

    private var durationJob: Job? = null
    private var callStartedAtMillis: Long? = null

    init {
        observeSessionState()
    }

    fun handleIntent(intent: CallIntent) {
        when (intent) {
            is CallIntent.Initialize -> initialize(
                callId = intent.callId,
                characterId = intent.characterId,
                direction = intent.direction,
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
        callId: Long,
        characterId: Long,
        direction: CallDirection,
    ) = intent {
        if (
            state.callId == callId &&
            state.characterId == characterId &&
            state.direction == direction
        ) {
            return@intent
        }

        reduce {
            state.copy(
                callId = callId,
                characterId = characterId,
                direction = direction,
            )
        }
    }

    private fun finishCall() = intent {
        if (state.phase == CallPhase.ENDING || state.phase == CallPhase.ENDED) return@intent

        val endedDurationSeconds = currentDurationSeconds()
        durationJob?.cancel()
        durationJob = null
        callStartedAtMillis = null

        reduce {
            state.copy(
                phase = CallPhase.ENDING,
                durationSeconds = endedDurationSeconds,
                endedDurationSeconds = endedDurationSeconds,
            )
        }
        try {
            callSessionRepository.endSession()
            reduce {
                state.copy(phase = CallPhase.ENDED)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (_: Throwable) {
            reduce {
                state.copy(phase = CallPhase.ERROR)
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
        val didStart = try {
            callSessionRepository.startSession()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (_: Throwable) {
            false
        }

        if (!didStart) {
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
            delay(CALL_ENDED_HOME_DELAY_MILLIS)
            postSideEffect(CallSideEffect.FinishCall)
            return@intent
        }

        callStartedAtMillis = SystemClock.elapsedRealtime()
        reduce {
            state.copy(
                phase = CallPhase.READY,
                durationSeconds = 0,
                endedDurationSeconds = null,
            )
        }
        startDurationTimer()
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

    private fun startDurationTimer() {
        if (durationJob?.isActive == true) return

        durationJob = viewModelScope.launch {
            while (isActive) {
                delay(DURATION_UPDATE_INTERVAL_MILLIS)
                val durationSeconds = currentDurationSeconds()
                intent {
                    if (state.phase != CallPhase.READY) return@intent

                    reduce {
                        state.copy(durationSeconds = durationSeconds)
                    }
                }
            }
        }
    }

    private fun currentDurationSeconds(): Int {
        val startedAtMillis = callStartedAtMillis ?: return 0
        return ((SystemClock.elapsedRealtime() - startedAtMillis) / 1000L)
            .coerceAtLeast(0L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
    }
}

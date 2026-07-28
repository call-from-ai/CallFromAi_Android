package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.state.CallIncomingState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CallIncomingViewModel @Inject constructor(
    private val callControlRepository: CallControlRepository,
) : ViewModel(),
    ContainerHost<CallIncomingState, CallIncomingSideEffect> {

    override val container: Container<CallIncomingState, CallIncomingSideEffect> = container(
        initialState = CallIncomingState(),
    )

    fun handleIntent(intent: CallIncomingIntent) {
        when (intent) {
            is CallIncomingIntent.Initialize -> initialize(
                callId = intent.callId,
                characterId = intent.characterId,
            )
            CallIncomingIntent.AcceptCall -> requestMicrophonePermission()
            is CallIncomingIntent.MicrophonePermissionResult -> {
                handleMicrophonePermissionResult(intent.isGranted)
            }
            CallIncomingIntent.RejectCall -> rejectCall()
        }
    }

    private fun initialize(
        callId: Long,
        characterId: Long,
    ) = intent {
        if (state.callId == callId && state.characterId == characterId) {
            return@intent
        }

        reduce {
            state.copy(
                callId = callId,
                characterId = characterId,
            )
        }
    }

    private fun requestMicrophonePermission() = intent {
        if (state.loadStatus == LoadStatus.Loading) return@intent

        val callId = state.callId
        if (callId <= 0L) {
            postSideEffect(
                CallIncomingSideEffect.ShowMessage("통화 정보를 확인할 수 없습니다."),
            )
            return@intent
        }

        reduce {
            state.copy(loadStatus = LoadStatus.Loading)
        }
        postSideEffect(
            CallIncomingSideEffect.RequestMicrophonePermission(callId),
        )
    }

    private fun handleMicrophonePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            acceptCall()
        } else {
            intent {
                reduce {
                    state.copy(loadStatus = LoadStatus.Idle)
                }
                postSideEffect(
                    CallIncomingSideEffect.ShowMessage(
                        "통화를 받으려면 마이크 권한이 필요합니다.",
                    ),
                )
            }
        }
    }

    private fun acceptCall() = intent {
        val callId = state.callId
        if (callId <= 0L) {
            reduce {
                state.copy(loadStatus = LoadStatus.Idle)
            }
            return@intent
        }

        try {
            callControlRepository.acceptCall(callId)
            reduce {
                state.copy(loadStatus = LoadStatus.Idle)
            }
            postSideEffect(
                CallIncomingSideEffect.NavigateToCall(
                    callId = callId,
                    characterId = state.characterId,
                ),
            )
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            val message = throwable.message ?: "전화를 받을 수 없습니다."
            reduce {
                state.copy(loadStatus = LoadStatus.Error(message))
            }
            postSideEffect(
                CallIncomingSideEffect.ShowMessage(message),
            )
        }
    }

    private fun rejectCall() = intent {
        if (state.loadStatus == LoadStatus.Loading) return@intent

        val callId = state.callId
        if (callId <= 0L) {
            postSideEffect(
                CallIncomingSideEffect.ShowMessage("통화 정보를 확인할 수 없습니다."),
            )
            return@intent
        }

        reduce {
            state.copy(loadStatus = LoadStatus.Loading)
        }

        try {
            callControlRepository.rejectCall(callId)
            reduce {
                state.copy(loadStatus = LoadStatus.Idle)
            }
            postSideEffect(CallIncomingSideEffect.Finish)
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            val message = throwable.message ?: "전화를 거절할 수 없습니다."
            reduce {
                state.copy(loadStatus = LoadStatus.Error(message))
            }
            postSideEffect(
                CallIncomingSideEffect.ShowMessage(message),
            )
        }
    }
}

package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kr.co.call.api.RING_TIMEOUT_MILLIS
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.state.CallIncomingState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kr.co.call.domain.exception.toLoadStatusError
import kr.co.call.impl.connection.PendingCallConnectionStore
import javax.inject.Inject

@HiltViewModel
class CallIncomingViewModel @Inject constructor(
    private val callControlRepository: CallControlRepository,
    private val callConnectionStore: PendingCallConnectionStore,
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
                characterName = intent.characterName,
                characterImageUrl = intent.characterImageUrl,
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
        characterName: String,
        characterImageUrl: String?,
    ) = intent {
        if (state.callId == callId && state.characterId == characterId) {
            return@intent
        }

        reduce {
            state.copy(
                callId = callId,
                characterId = characterId,
                character = CallCharacterUiModel(
                    name = characterName,
                    profileImageUrl = characterImageUrl,
                ),
                isResolved = false,
                loadStatus = LoadStatus.Idle,
            )
        }

        // 벨소리 타임아웃: 이 시간 안에 수락/거절이 없으면 화면을 내림
        delay(RING_TIMEOUT_MILLIS)
        if (state.callId == callId && !state.isResolved) {
            postSideEffect(CallIncomingSideEffect.Finish)
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
        // 착신 정보 초기화되지 않은 경우에 UI 상태 복구
        if (callId <= 0L) {
            reduce {
                state.copy(loadStatus = LoadStatus.Idle)
            }
            postSideEffect(
                CallIncomingSideEffect.ShowMessage(
                    "통화 정보를 확인할 수 없습니다.",
                ),
            )
            return@intent
        }

        try {
            val connectionInfo = callControlRepository.acceptCall(callId)
            // 통화 화면의 뷰모델이 wsTicket을 사용할 수 있도록 메모리 Store에 연결 정보 임시 보관
            callConnectionStore.save(connectionInfo)
            reduce {
                state.copy(isResolved = true, loadStatus = LoadStatus.Idle)
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
            val errorStatus = throwable.toLoadStatusError(
                defaultMessage = "전화를 받을 수 없습니다.",
            )

            reduce {
                state.copy(loadStatus = errorStatus)
            }

            // LoadStatus에 저장한 것과 동일한 사용자용 메시지를 표시
            postSideEffect(
                CallIncomingSideEffect.ShowMessage(
                    errorStatus.message,
                ),
            )
        }
    }

    private fun rejectCall() = intent {
        // 이미 수락 또는 거절 요청 처리하고 있다면 추가 요청을 보내지 않음
        if (state.loadStatus == LoadStatus.Loading) return@intent

        val callId = state.callId
        if (callId <= 0L) {
            postSideEffect(
                CallIncomingSideEffect.ShowMessage("통화 정보를 확인할 수 없습니다."),
            )
            return@intent
        }

        // 거절 API가 끝날 때까지 수락,거절 버튼 비활성화
        reduce {
            state.copy(loadStatus = LoadStatus.Loading)
        }

        try {
            // 통화 거절
            callControlRepository.rejectCall(callId)
            reduce {
                state.copy(loadStatus = LoadStatus.Idle, isResolved = true)
            }
            postSideEffect(CallIncomingSideEffect.Finish)
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            val errorStatus = throwable.toLoadStatusError(
                defaultMessage = "전화를 거절할 수 없습니다.",
            )
            reduce {
                state.copy(loadStatus = errorStatus)
            }
            postSideEffect(
                CallIncomingSideEffect.ShowMessage(errorStatus.message),
            )
        }
    }

}

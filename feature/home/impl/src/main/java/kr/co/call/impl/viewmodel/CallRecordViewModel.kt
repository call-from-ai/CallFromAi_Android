package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.mapper.toUiModel
import kr.co.call.impl.viewmodel.state.CallRecordState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class CallRecordViewModel @Inject constructor(
    private val callRecordRepository: CallRecordRepository,
) : ViewModel(), ContainerHost<CallRecordState, CallRecordSideEffect> {

    override val container: Container<CallRecordState, CallRecordSideEffect> = container(
        initialState = CallRecordState(),
    )

    fun handleIntent(intent: CallRecordIntent) {
        when (intent) {
            is CallRecordIntent.Load -> loadCallRecord(callId = intent.callId)
            CallRecordIntent.Retry -> retryLoad()
        }
    }

    private fun retryLoad() = intent {
        val callId = state.callId ?: return@intent
        loadCallRecord(callId = callId)
    }

    private fun loadCallRecord(callId: Long) = intent {
        reduce {
            state.copy(
                callId = callId,
                loadStatus = LoadStatus.Loading,
            )
        }
        try {
            // callInfo와 callScript를 동시에 가져오기
            val (callInfo, callScript) = coroutineScope {
                val callInfoDeferred = async {
                    callRecordRepository.getCallInfo(callId).getOrThrow()
                }

                val callScriptDeferred = async {
                    callRecordRepository.getCallScript(callId).getOrThrow()
                }

                callInfoDeferred.await() to callScriptDeferred.await()
            }

            // state에 저장
            reduce {
                state.copy(
                    record = callInfo.toUiModel(),
                    transcripts = callScript,
                    loadStatus = LoadStatus.Idle,
                )
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            val message = throwable.message ?: "통화 기록을 불러오지 못했습니다."
            reduce {
                state.copy(
                    loadStatus = LoadStatus.Error(
                        message = message,
                    ),
                )
            }
            postSideEffect(
                CallRecordSideEffect.ShowMessage(
                    message = message,
                ),
            )
        }
    }
}

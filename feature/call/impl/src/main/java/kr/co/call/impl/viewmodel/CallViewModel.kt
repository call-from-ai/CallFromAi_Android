package kr.co.call.impl.viewmodel

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.call.CallStreamingEvent
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.impl.audio.CallAudioPlayer
import kr.co.call.impl.audio.CallAudioRecorder
import kr.co.call.impl.connection.PendingCallConnectionStore
import kr.co.call.impl.session.CallSessionManager
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

// 통화 종료 후 홈 이동 지연 시간
private const val CALL_ENDED_HOME_DELAY_MILLIS = 3000L
private const val DURATION_UPDATE_INTERVAL_MILLIS = 1000L

@HiltViewModel
class CallViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val callSessionManager: CallSessionManager,
    private val callControlRepository: CallControlRepository,
    private val callRecordRepository: CallRecordRepository,
    private val pendingCallConnectionStore: PendingCallConnectionStore,
    private val callAudioPlayer: CallAudioPlayer,
    private val callAudioRecorder: CallAudioRecorder,
) :
    ViewModel(),
    ContainerHost<CallState, CallSideEffect> {

    override val container: Container<CallState, CallSideEffect> = container(
        initialState = CallState(),
    )

    val sessionState = callSessionManager.sessionState

    // 캐릭터 이름·사진 (CallState와 별개 수명)
    private val _character = MutableStateFlow(CallCharacterUiModel())
    val character = _character.asStateFlow()

    private var durationJob: Job? = null

    init {
        // 소켓 이벤트 → 화면 상태 반영
        viewModelScope.launch {
            callSessionManager.streamingEvents.collect { event ->
                handleStreamingEvent(event)
            }
        }
    }

    // 정상 종료 경로 없이 화면이 사라지는 경우의 안전망
    override fun onCleared() {
        super.onCleared()

        val phase = container.stateFlow.value.phase
        if (phase == CallPhase.ENDING || phase == CallPhase.ENDED) {
            return
        }

        Timber.w("CallViewModel onCleared: 정상 종료 경로 없이 화면 이탈 - 방어적으로 자원 정리")
        runCatching { callAudioPlayer.stop() }
        runCatching { callAudioRecorder.stop() }
        // viewModelScope 취소 이후라 runBlocking으로 로컬 정리
        runBlocking {
            runCatching { callSessionManager.endSession() }
        }
    }

    fun handleIntent(intent: CallIntent) {
        when (intent) {
            is CallIntent.Initialize -> initialize(
                callId = intent.callId,
                characterId = intent.characterId,
                isIncoming = intent.isIncoming,
                characterName = intent.characterName,
                characterImageUrl = intent.characterImageUrl,
            )
            is CallIntent.MicrophonePermissionResult -> {
                handleMicrophonePermissionResult(intent.isGranted)
            }
            CallIntent.CallConnected -> handleCallConnected()
            CallIntent.EndCall -> finishCall()
            CallIntent.ToggleMicrophone -> toggleMicrophone()
            CallIntent.ToggleSpeaker -> toggleSpeaker()
        }
    }

    private fun initialize(
        callId: Long,
        characterId: Long,
        isIncoming: Boolean,
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
                isIncoming = isIncoming,
            )
        }

        if (isIncoming && characterId > 0L) {
            // 착신은 메인 아닐 수 있어 characterId로 직접 조회
            runCatching { callControlRepository.getCharacter(characterId) }
                .onSuccess { character ->
                    _character.value = CallCharacterUiModel(
                        name = character.name,
                        profileImageUrl = character.imageUrl,
                    )
                    preloadProfileImage(character.imageUrl)
                }
        } else if (characterName.isNotBlank()) {
            // 발신은 Home이 넘긴 이름·사진 그대로 사용
            _character.value = CallCharacterUiModel(
                name = characterName,
                profileImageUrl = characterImageUrl,
            )
            preloadProfileImage(characterImageUrl)
        }
    }

    // 프로필 이미지 프리로드 (첫 통화 진입 시 로딩 지연 방지)
    private fun preloadProfileImage(url: String?) {
        val imageUrl = url?.takeIf { it.isNotBlank() } ?: return
        context.imageLoader.enqueue(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .build(),
        )
    }

    private fun finishCall() = intent {
        if (state.phase == CallPhase.ENDING || state.phase == CallPhase.ENDED) return@intent

        val endedDurationSeconds = currentDurationSeconds()
        val callId = state.callId
        val hasReceivedCallReady = state.hasReceivedCallReady
        durationJob?.cancel()
        durationJob = null

        // 종료 처리 완료까지 ENDING 상태 유지
        reduce {
            state.copy(
                phase = CallPhase.ENDING,
                startedAtMillis = null,
                durationSeconds = endedDurationSeconds,
            )
        }

        try {
            if (callId > 0L) {
                callControlRepository.endCall(callId)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            Timber.e(throwable, "endCall API 실패: callId=%d", callId)
            // API 실패해도 로컬 자원은 항상 정리
            runCatching { callSessionManager.endSession() }
            runCatching { callAudioPlayer.stop() }
            runCatching { callAudioRecorder.stop() }
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
            delay(CALL_ENDED_HOME_DELAY_MILLIS)
            postSideEffect(CallSideEffect.FinishCall)
            return@intent
        }

        runCatching { callSessionManager.endSession() }
        runCatching { callAudioPlayer.stop() }
        runCatching { callAudioRecorder.stop() }

        // CALL_READY 없었으면 산출물 없어 조회 생략, 요약 실패는 종료를 막지 않음
        val callInfo = if (hasReceivedCallReady && callId > 0L) {
            reduce { state.copy(isPreparingSummary = true) }
            fetchCallInfo(callId).also { result ->
                if (result == null) {
                    postSideEffect(CallSideEffect.ShowMessage("통화 요약을 불러오지 못했어요"))
                }
            }
        } else {
            null
        }

        reduce {
            state.copy(
                phase = if (hasReceivedCallReady) CallPhase.ENDED else CallPhase.ERROR,
                isPreparingSummary = false,
                callInfo = callInfo,
            )
        }
        delay(CALL_ENDED_HOME_DELAY_MILLIS)
        postSideEffect(CallSideEffect.FinishCall)
    }

    private fun toggleMicrophone() = intent {
        callSessionManager.setMicrophoneEnabled(
            enabled = !sessionState.value.isMicrophoneEnabled,
        )
    }

    private fun toggleSpeaker() = intent {
        callSessionManager.setSpeakerEnabled(
            enabled = !sessionState.value.isSpeakerEnabled,
        )
    }

    private fun startSession() = intent {
        // 발신 미다이얼 상태면 권한 확정 시점에 발신 API 호출
        if (!state.isIncoming && state.callId <= 0L) {
            val connectionInfo = runCatching { callControlRepository.startCall(state.characterId) }
                .getOrElse { throwable ->
                    if (throwable is CancellationException) throw throwable
                    Timber.e(throwable, "발신 API(startCall) 실패: characterId=%d", state.characterId)
                    // 발신 거절 시 화면 유지 없이 토스트 후 종료
                    postSideEffect(
                        CallSideEffect.ShowMessage(
                            throwable.toUserMessage("전화를 걸 수 없습니다."),
                        ),
                    )
                    postSideEffect(CallSideEffect.FinishCall)
                    return@intent
                }

            pendingCallConnectionStore.save(connectionInfo)
            reduce {
                state.copy(callId = connectionInfo.callId)
            }
            // 이름·사진은 initialize()에서 이미 세팅됨
        }

        // 1회용 wsTicket 소비 (착신은 수락 시 저장분, 발신은 방금 저장분)
        val wsTicket = pendingCallConnectionStore.consume(state.callId)?.wsTicket
        if (wsTicket == null) {
            Timber.e("wsTicket 없음: callId=%d (PendingCallConnectionStore에 저장된 티켓 없음)", state.callId)
        }

        val didStart = if (wsTicket == null) {
            false
        } else {
            try {
                val sessionStarted = callSessionManager.startSession(wsTicket)
                if (sessionStarted) {
                    callAudioPlayer.start()
                    callAudioRecorder.start()
                } else {
                    Timber.e("callSessionManager.startSession() 실패: callId=%d", state.callId)
                }
                sessionStarted
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (throwable: Throwable) {
                Timber.e(throwable, "세션/플레이어 초기화 실패: callId=%d", state.callId)
                // 세션 성공·플레이어 실패 케이스 포함 방어적 정리
                runCatching { callSessionManager.endSession() }
                runCatching { callAudioPlayer.stop() }
                runCatching { callAudioRecorder.stop() }
                false
            }
        }

        if (!didStart) {
            reduce {
                state.copy(phase = CallPhase.ERROR)
            }
            delay(CALL_ENDED_HOME_DELAY_MILLIS)
            postSideEffect(CallSideEffect.FinishCall)
            return@intent
        }

    }

    private fun handleStreamingEvent(event: CallStreamingEvent) {
        when (event) {
            is CallStreamingEvent.Ready -> {
                callAudioRecorder.onCallReady()
                handleIntent(CallIntent.CallConnected)
            }
            is CallStreamingEvent.Ended -> {
                if (event.callId == container.stateFlow.value.callId) {
                    finishCallFromServerEvent()
                }
            }
            is CallStreamingEvent.Failed -> finishCallFromServerEvent()
            is CallStreamingEvent.SpeechCanceled -> {
                Timber.d("AI_SPEECH_CANCELED 수신: callId=%d", event.callId)
                callAudioPlayer.clear()
            }
            is CallStreamingEvent.AudioReceived -> {
                Timber.d(
                    "AudioReceived 수신: wavSize=%d, callId=%d",
                    event.wav.size,
                    container.stateFlow.value.callId,
                )
                callAudioPlayer.enqueue(event.wav)
            }
        }
    }

    // 서버 종료 시 end API 재호출 안 함, CALL_READY 있었으면 정상 종료로 취급
    private fun finishCallFromServerEvent() = intent {
        if (state.phase == CallPhase.ENDING || state.phase == CallPhase.ENDED) return@intent
        val endedDurationSeconds = currentDurationSeconds()
        val callId = state.callId
        val hasReceivedCallReady = state.hasReceivedCallReady
        durationJob?.cancel()
        durationJob = null

        reduce {
            state.copy(
                phase = CallPhase.ENDING,
                startedAtMillis = null,
                durationSeconds = endedDurationSeconds,
            )
        }

        runCatching { callSessionManager.endSession() }
        runCatching { callAudioPlayer.stop() }
        runCatching { callAudioRecorder.stop() }

        val callInfo = if (hasReceivedCallReady && callId > 0L) {
            reduce { state.copy(isPreparingSummary = true) }
            fetchCallInfo(callId).also { result ->
                if (result == null) {
                    postSideEffect(CallSideEffect.ShowMessage("통화 요약을 불러오지 못했어요"))
                }
            }
        } else {
            null
        }

        reduce {
            state.copy(
                phase = if (hasReceivedCallReady) CallPhase.ENDED else CallPhase.ERROR,
                isPreparingSummary = false,
                callInfo = callInfo,
            )
        }
        delay(CALL_ENDED_HOME_DELAY_MILLIS)
        postSideEffect(CallSideEffect.FinishCall)
    }

    // wait=true 1회 호출, 서버 5초 대기 안에 안 끝나면 PROCESSING 상태 그대로 반환
    private suspend fun fetchCallInfo(callId: Long): CallInfo? =
        callRecordRepository.getCallInfo(callId, wait = true)
            .onFailure { throwable ->
                Timber.e(throwable, "통화 요약 조회 실패: callId=%d", callId)
            }
            .getOrNull()

    private fun handleCallConnected() = intent {
        if (state.phase != CallPhase.CONNECTING) return@intent

        val startedAtMillis = SystemClock.elapsedRealtime()
        reduce {
            state.copy(
                phase = CallPhase.READY,
                startedAtMillis = startedAtMillis,
                durationSeconds = 0,
                hasReceivedCallReady = true,
            )
        }
        startDurationTimer()
    }

    private fun handleMicrophonePermissionResult(isGranted: Boolean) {
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
        val startedAtMillis = container.stateFlow.value.startedAtMillis ?: return 0
        return ((SystemClock.elapsedRealtime() - startedAtMillis) / 1000L)
            .coerceAtLeast(0L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
    }
}

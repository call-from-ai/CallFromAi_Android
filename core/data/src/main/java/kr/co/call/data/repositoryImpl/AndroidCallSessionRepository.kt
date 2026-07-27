package kr.co.call.data.repositoryImpl

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kr.co.call.domain.model.call.CallAudioFocusState
import kr.co.call.domain.model.call.CallSessionState
import kr.co.call.domain.repository.CallSessionRepository
import kr.co.call.domain.repository.CallStreamingRepository

/**
 * 전역 오디오 세션 관리
 * - 해당 부분은 소켓 연결 시 수정 진행하겠습니다
 */
@Singleton
class AndroidCallSessionRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val streamingRepository: CallStreamingRepository,
) : CallSessionRepository {

    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val sessionMutex = Mutex()

    // 통화 세션 상태 단일 소스
    private val _sessionState = MutableStateFlow(CallSessionState())
    override val sessionState: StateFlow<CallSessionState> = _sessionState.asStateFlow()

    // 오디오 포커스 상태 단일 소스
    private val _audioFocusState = MutableStateFlow(CallAudioFocusState.IDLE)
    override val audioFocusState: StateFlow<CallAudioFocusState> = _audioFocusState.asStateFlow()

    private var isSessionActive = false
    private var isCommunicationDeviceListenerRegistered = false

    // 시스템 오디오 포커스 변화 반영
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        _audioFocusState.value = when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> CallAudioFocusState.GAINED
            AudioManager.AUDIOFOCUS_LOSS -> CallAudioFocusState.LOST
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            -> CallAudioFocusState.LOST_TRANSIENT
            else -> _audioFocusState.value
        }
    }

    // VoIP 통화용 오디오 포커스 요청 구성
    private val audioFocusRequest = AudioFocusRequest.Builder(
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
    )
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        .setOnAudioFocusChangeListener(
            audioFocusChangeListener,
            Handler(Looper.getMainLooper()),
        )
        .build()

    // 실제 통화 출력 장치 변화 반영
    private val communicationDeviceChangedListener =
        AudioManager.OnCommunicationDeviceChangedListener { device ->
            _sessionState.update {
                it.copy(isSpeakerEnabled = device?.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
            }
        }

    override suspend fun startSession(): Boolean = sessionMutex.withLock {
        // 중복 세션 시작 방지
        if (isSessionActive) return@withLock true

        // 통화 오디오 포커스 선점
        val focusResult = audioManager.requestAudioFocus(audioFocusRequest)
        if (focusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            _audioFocusState.value = CallAudioFocusState.LOST
            return@withLock false
        }

        // VoIP 통화 모드와 장치 감지 활성화
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        registerCommunicationDeviceListener()
        isSessionActive = true
        _sessionState.value = CallSessionState()
        _audioFocusState.value = CallAudioFocusState.GAINED
        true
    }

    override suspend fun setMicrophoneEnabled(enabled: Boolean) {
        // 로컬 마이크 전송 상태 동기화
        streamingRepository.setMicrophoneEnabled(enabled)
        _sessionState.update { it.copy(isMicrophoneEnabled = enabled) }
    }

    override suspend fun setSpeakerEnabled(enabled: Boolean): Boolean = sessionMutex.withLock {
        // Android 버전별 스피커 출력 경로 전환
        val wasChanged = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setCommunicationSpeakerEnabled(enabled)
        } else {
            @Suppress("DEPRECATION")
            audioManager.isSpeakerphoneOn = enabled
            true
        }

        // 출력 경로 전환 성공 상태 반영
        if (wasChanged) {
            _sessionState.update { it.copy(isSpeakerEnabled = enabled) }
        }
        wasChanged
    }

    override suspend fun endSession() = sessionMutex.withLock {
        try {
            // 통화 출력 장치와 리스너 해제
            unregisterCommunicationDeviceListener()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                audioManager.clearCommunicationDevice()
            } else {
                @Suppress("DEPRECATION")
                audioManager.isSpeakerphoneOn = false
            }

            // 오디오 포커스와 통화 모드 반환
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
            audioManager.mode = AudioManager.MODE_NORMAL
            streamingRepository.close()
        } finally {
            // 종료 결과와 무관한 세션 상태 초기화
            isSessionActive = false
            _sessionState.value = CallSessionState()
            _audioFocusState.value = CallAudioFocusState.IDLE
        }
    }

    private fun registerCommunicationDeviceListener() {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            isCommunicationDeviceListenerRegistered
        ) {
            return
        }

        audioManager.addOnCommunicationDeviceChangedListener(
            ContextCompat.getMainExecutor(context),
            communicationDeviceChangedListener,
        )
        isCommunicationDeviceListenerRegistered = true
    }

    private fun unregisterCommunicationDeviceListener() {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            !isCommunicationDeviceListenerRegistered
        ) {
            return
        }

        audioManager.removeOnCommunicationDeviceChangedListener(
            communicationDeviceChangedListener,
        )
        isCommunicationDeviceListenerRegistered = false
    }

    private fun setCommunicationSpeakerEnabled(enabled: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false

        if (!enabled) {
            // 내장 스피커 강제 선택 해제
            audioManager.clearCommunicationDevice()
            return true
        }

        // 사용 가능한 내장 스피커 탐색
        val speaker = audioManager.availableCommunicationDevices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
        } ?: return false

        return audioManager.setCommunicationDevice(speaker)
    }
}

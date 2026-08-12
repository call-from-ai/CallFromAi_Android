package kr.co.call.impl.session

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kr.co.call.domain.model.call.CallAudioFocusState
import kr.co.call.domain.model.call.CallSessionState
import kr.co.call.domain.model.call.CallStreamingEvent
import kr.co.call.domain.repository.CallStreamingRepository
import timber.log.Timber

/**
 * Android의 오디오 포커스와 통화 출력 장치를 관리합니다.
 */
@Singleton
class AndroidCallSessionManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val streamingRepository: CallStreamingRepository,
) : CallSessionManager {

    companion object {
        private const val TAG = "AndroidCallSessionManager"
    }

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
    private var isBluetoothScoReceiverRegistered = false

    // 세션 수명 동안만 통화 소켓을 구독하는 스코프
    private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var streamingJob: Job? = null

    private val _streamingEvents = MutableSharedFlow<CallStreamingEvent>(extraBufferCapacity = 8)
    override val streamingEvents: Flow<CallStreamingEvent> = _streamingEvents.asSharedFlow()

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

    // 실제 통화 출력 장치 변화 반영 (API 31 이상)
    private val communicationDeviceChangedListener =
        AudioManager.OnCommunicationDeviceChangedListener { device ->
            _sessionState.update {
                it.copy(
                    isSpeakerEnabled = device?.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
                    isBluetoothConnected = device?.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                )
            }
        }

    // 블루투스 SCO 연결 상태 변화 반영 (API 31 미만 전용 - startBluetoothSco()는 비동기 연결)
    private val bluetoothScoStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(receiverContext: Context, intent: Intent) {
            val scoState = intent.getIntExtra(
                AudioManager.EXTRA_SCO_AUDIO_STATE,
                AudioManager.SCO_AUDIO_STATE_ERROR,
            )
            _sessionState.update {
                it.copy(isBluetoothConnected = scoState == AudioManager.SCO_AUDIO_STATE_CONNECTED)
            }
        }
    }

    override suspend fun startSession(
        wsTicket: String,
    ): Boolean = sessionMutex.withLock {
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
        connectBluetoothScoIfAvailable()
        isSessionActive = true
        _sessionState.value = CallSessionState()
        _audioFocusState.value = CallAudioFocusState.GAINED

        // 통화 소켓 연결과 이벤트 구독 시작
        streamingJob = sessionScope.launch {
            streamingRepository.connect(wsTicket).collect { event ->
                _streamingEvents.emit(event)
            }
        }
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
            // 통화 소켓 구독 종료
            streamingJob?.cancel()
            streamingJob = null

            // 통화 출력 장치와 리스너 해제
            unregisterCommunicationDeviceListener()
            disconnectBluetoothSco()
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

    /**
     * 연결된 블루투스 헤드셋이 있으면 통화 오디오를 그쪽으로 라우팅합니다.
     * - API 31 이상: setCommunicationDevice()로 명시적 디바이스 선택 (공식 권장 방식)
     * - API 30 이하: startBluetoothSco()로 SCO 링크를 직접 열어야 함
     * 권한이 없거나 연결된 기기가 없으면 조용히 건너뛰고 기본 라우팅(수화구)을 유지합니다.
     */
    private fun connectBluetoothScoIfAvailable() {
        if (!hasBluetoothConnectPermission()) {
            Timber.tag(TAG).d("BLUETOOTH_CONNECT 권한 없음 - 블루투스 라우팅 건너뜀")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothDevice = audioManager.availableCommunicationDevices.firstOrNull {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            } ?: return
            audioManager.setCommunicationDevice(bluetoothDevice)
        } else {
            // 기기가 통화 중이 아닌 상태에서 SCO를 열 수 있는지 사전 확인
            @Suppress("DEPRECATION")
            if (!audioManager.isBluetoothScoAvailableOffCall) return

            // API 33 이상에서 요구하는 exported 플래그까지 안전하게 처리해주는 호환 API 사용
            ContextCompat.registerReceiver(
                context,
                bluetoothScoStateReceiver,
                IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            isBluetoothScoReceiverRegistered = true

            // startBluetoothSco()는 비동기 요청 - 실제 연결 결과는 위 리시버로 전달됨
            @Suppress("DEPRECATION")
            audioManager.startBluetoothSco()
            @Suppress("DEPRECATION")
            audioManager.isBluetoothScoOn = true
        }
    }

    private fun disconnectBluetoothSco() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31 이상은 endSession()의 clearCommunicationDevice()가 처리
            return
        }

        if (isBluetoothScoReceiverRegistered) {
            context.unregisterReceiver(bluetoothScoStateReceiver)
            isBluetoothScoReceiverRegistered = false
        }

        @Suppress("DEPRECATION")
        audioManager.isBluetoothScoOn = false
        @Suppress("DEPRECATION")
        audioManager.stopBluetoothSco()
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        // API 31 미만은 BLUETOOTH가 일반 권한이라 매니페스트 선언만으로 자동 허용됨
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

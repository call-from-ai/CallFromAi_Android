package kr.co.call.impl.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.call.domain.repository.CallStreamingRepository
import timber.log.Timber

/**
 * 마이크를 캡처해 서버로 전송합니다.
 *
 * CALL_READY 전까지 캡처한 PCM은 버퍼에 보관하며,
 * [onCallReady] 호출 시점에 버퍼를 순서대로 flush한 뒤 실시간 전송으로 전환합니다.
 */
@Singleton
class CallAudioRecorder @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val streamingRepository: CallStreamingRepository,
) {

    // 캡처 루프 전용 스코프
    private val recorderScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var captureJob: Job? = null

    private var audioRecord: AudioRecord? = null

    @Volatile
    private var isReady = false

    // CALL_READY 전 캡처분 임시 보관
    private val pendingBuffer = mutableListOf<ByteArray>()

    // 권한 확인 + AudioRecord 생성 + 캡처 루프 시작
    fun start() {
        Timber.tag(TAG).d("recorder start() 호출: 기존 audioRecord=%s, captureJob=%s", audioRecord, captureJob)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.tag(TAG).w("RECORD_AUDIO 권한 없음 - 녹음 시작 안 함")
            return
        }

        // 이전 캡처가 남아있으면 방어적으로 정리, isReady는 여기서 체크하지 않음
        captureJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        pendingBuffer.clear()

        captureJob = recorderScope.launch {
            val record = createAndStartRecording()
            if (record == null) {
                Timber.tag(TAG).e("AudioRecord 시작 실패 - %d회 재시도 후 포기", MAX_START_ATTEMPTS)
                return@launch
            }
            audioRecord = record

            val chunk = ByteArray(CHUNK_SIZE_BYTES)
            var hasFlushedPending = false
            var frameCount = 0
            var consecutiveErrorCount = 0

            while (true) {
                val readSize = record.read(chunk, 0, chunk.size)
                if (readSize == AudioRecord.ERROR_DEAD_OBJECT) {
                    Timber.tag(TAG).e("AudioRecord ERROR_DEAD_OBJECT - 캡처 루프 종료")
                    break
                }
                if (readSize <= 0) {
                    consecutiveErrorCount++
                    Timber.tag(TAG).w(
                        "read() 비정상 반환: readSize=%d (연속 %d회)",
                        readSize, consecutiveErrorCount,
                    )
                    if (consecutiveErrorCount >= MAX_CONSECUTIVE_READ_ERRORS) {
                        Timber.tag(TAG).e("read() 연속 실패 한도 초과 - 캡처 루프 종료")
                        break
                    }
                    delay(READ_ERROR_RETRY_DELAY_MILLIS)
                    continue
                }
                consecutiveErrorCount = 0
                val pcm = chunk.copyOf(readSize)
                frameCount++
                if (frameCount % 50 == 0) {
                    // 20ms 청크 기준 약 1초에 한 번만 로그 (스팸 방지)
                    Timber.tag(TAG).d(
                        "캡처 진행 중: frame=%d, isReady=%s, readSize=%d",
                        frameCount, isReady, readSize,
                    )
                }

                if (!isReady) {
                    // CALL_READY 전이면 버퍼링만
                    pendingBuffer.add(pcm)
                    continue
                }

                if (!hasFlushedPending) {
                    // CALL_READY 직후 1회: 버퍼링해둔 것부터 순서대로 전송
                    Timber.tag(TAG).d("pendingBuffer flush: 버퍼링된 %d개 프레임 전송", pendingBuffer.size)
                    pendingBuffer.forEach { buffered -> streamingRepository.sendAudio(buffered) }
                    pendingBuffer.clear()
                    hasFlushedPending = true
                }

                streamingRepository.sendAudio(pcm)
            }
            Timber.tag(TAG).d("recorder 캡처 루프 종료: 총 %d프레임", frameCount)
        }
    }

    // AudioRecord 생성 + startRecording()이 실제로 RECORDING 상태로 전환됐는지 확인 후, 실패하면 재시도
    private suspend fun createAndStartRecording(): AudioRecord? {
        // 코루틴 실행 중 권한이 취소되었을 경우를 방지하기 위해 녹음 시작 할 때 권한 확인
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.tag(TAG).w("RECORD_AUDIO 권한 없음 - AudioRecord 생성 취소")
            return null
        }

        val minBufferSize = AudioRecord.getMinBufferSize(
            CAPTURE_SAMPLE_RATE_HZ,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        repeat(MAX_START_ATTEMPTS) { attempt ->
            val record = AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                CAPTURE_SAMPLE_RATE_HZ,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
            )
            Timber.tag(TAG).d(
                "recorder 생성 시도 #%d: minBufferSize=%d, state=%d",
                attempt + 1, minBufferSize, record.state,
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                record.release()
                delay(START_RETRY_DELAY_MILLIS)
                return@repeat
            }

            record.startRecording()
            Timber.tag(TAG).d(
                "startRecording() 호출 후 recordingState=%d (시도 #%d)",
                record.recordingState, attempt + 1,
            )

            if (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                return record
            }

            record.stop()
            record.release()
            delay(START_RETRY_DELAY_MILLIS)
        }

        return null
    }

    // CALL_READY 수신, 캡처 루프가 다음 청크부터 버퍼를 flush하도록 플래그만 전환
    fun onCallReady() {
        Timber.tag(TAG).d("recorder onCallReady() 호출: pendingBuffer=%d", pendingBuffer.size)
        isReady = true
    }

    // 마이크 캡처 루프 종료 + AudioRecord 해제
    fun stop() {
        Timber.tag(TAG).d("recorder stop() 호출: audioRecord=%s, captureJob=%s", audioRecord, captureJob)
        captureJob?.cancel()
        captureJob = null

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        isReady = false
        pendingBuffer.clear()
    }

    private companion object {
        const val TAG = "CallAudio"

        // 마이크 캡처 샘플레이트
        const val CAPTURE_SAMPLE_RATE_HZ = 16_000

        // 20ms 청크
        const val CHUNK_SIZE_BYTES = CAPTURE_SAMPLE_RATE_HZ / 50 * 2

        // 직전 통화 종료 직후 오디오 HAL이 아직 안 안정된 상태에서 AudioRecord를 재생성하면
        // state/recordingState 전환이 실패할 수 있어 짧게 재시도
        const val MAX_START_ATTEMPTS = 3
        const val START_RETRY_DELAY_MILLIS = 150L

        // read()가 연속으로 실패할 때 busy-loop을 피하기 위한 재시도 한도
        const val MAX_CONSECUTIVE_READ_ERRORS = 25
        const val READ_ERROR_RETRY_DELAY_MILLIS = 20L
    }
}

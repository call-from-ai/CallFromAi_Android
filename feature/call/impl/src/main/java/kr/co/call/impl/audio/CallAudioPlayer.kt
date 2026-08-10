package kr.co.call.impl.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 서버가 보내는 WAV 오디오 프레임을 순서대로 재생합니다.
 *
 * 프레임은 [enqueue]로 큐에 쌓이는 순서대로 재생되며,
 * AI_SPEECH_CANCELED 수신 시 [clear]로 대기 프레임 폐기 + 현재 재생 중단이 이뤄집니다.
 */
@Singleton
class CallAudioPlayer @Inject constructor() {

    // 재생 루프 전용 스코프
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var playbackJob: Job? = null

    // WAV 프레임 순서 보장용 큐
    private var queue = Channel<ByteArray>(Channel.UNLIMITED)

    private var audioTrack: AudioTrack? = null

    // AudioTrack 생성 + 큐 소비 루프 시작
    fun start() {
        Timber.tag(TAG).d("start() 호출: 기존 audioTrack=%s, playbackJob=%s", audioTrack, playbackJob)
        // 기존 오디오트랙 정리
        audioTrack?.let {
            it.stop()
            it.release()
        }
        audioTrack = null
        // 새로운 오디오트랙 생성
        val minBufferSize = AudioTrack.getMinBufferSize(
            PLAYBACK_SAMPLE_RATE_HZ,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(PLAYBACK_SAMPLE_RATE_HZ)      // 44100
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) // OUT!
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        // 재생 가능한 상태로 전환
        val playState = audioTrack?.play()
        Timber.tag(TAG).d("audioTrack 생성 완료: minBufferSize=%d, state=%s", minBufferSize, audioTrack?.state)
        // 큐 초기화
        queue = Channel(Channel.UNLIMITED)
        val newQueue = queue
        playbackJob?.cancel()
        // 재생 루프 시작
        playbackJob = playerScope.launch {
            var frameCount = 0
            for (wav in newQueue) {
                try {
                    val pcm = extractPcmFromWav(wav) // PCM 추출
                    val written = audioTrack?.write(pcm, 0, pcm.size) // audioTrack에 쓰기
                    frameCount++
                    Timber.tag(TAG).d(
                        "프레임 재생 #%d: wavSize=%d, pcmSize=%d, written=%s, trackState=%s, playState=%s",
                        frameCount, wav.size, pcm.size, written, audioTrack?.state, audioTrack?.playState,
                    )
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (throwable: Throwable) {
                    // 프레임 하나가 깨져도 재생 루프 자체는 죽지 않도록 방어
                    Timber.tag(TAG).e(throwable, "프레임 재생 실패: wavSize=%d", wav.size)
                }
            }
            Timber.tag(TAG).d("재생 루프 종료: 총 %d프레임", frameCount)
        }
    }

    // 수신한 WAV 프레임 큐에 추가
    fun enqueue(wav: ByteArray) {
        val result = queue.trySend(wav)
        Timber.tag(TAG).d("enqueue: wavSize=%d, success=%s", wav.size, result.isSuccess)
    }

    // 대기 프레임 종료 + 현재 재생 중단 (AI_SPEECH_CANCELED)
    fun clear() {
        // 대기 중인, 아직 재생 안한 프레임 종료
        while(queue.tryReceive().isSuccess){
        }
        // 스피커에서 나오는 소리 즉시 재생 중단
        audioTrack?.pause()
        audioTrack?.flush()
        audioTrack?.play() // 재생 가능한 상태로 전환
    }

    // 큐 소비 루프 종료 + AudioTrack 해제
    fun stop() {
        Timber.tag(TAG).d("stop() 호출: audioTrack=%s, playbackJob=%s", audioTrack, playbackJob)
        playbackJob?.cancel() // 루프 종료
        playbackJob = null
        audioTrack?.stop() // 재생 중인 오디오트랙 정지
        audioTrack?.release()
        audioTrack = null
    }

    // WAV RIFF 청크를 순회하며 data 청크를 찾아 PCM만 추출
    private fun extractPcmFromWav(wav: ByteArray): ByteArray {
        if (wav.size < RIFF_HEADER_SIZE) return ByteArray(0)

        var offset = RIFF_HEADER_SIZE
        while (offset + CHUNK_HEADER_SIZE <= wav.size) {
            val chunkId = String(wav, offset, 4, Charsets.US_ASCII)
            // RIFF 청크 크기는 unsigned 32bit이라 Int로 계산하면 상위 비트가 설 때 음수로
            // 뒤집혀 offset이 후퇴할 수 있으므로 Long으로 계산해 부호 오버플로우를 방지
            val chunkSize = (wav[offset + 4].toLong() and 0xFF) or
                ((wav[offset + 5].toLong() and 0xFF) shl 8) or
                ((wav[offset + 6].toLong() and 0xFF) shl 16) or
                ((wav[offset + 7].toLong() and 0xFF) shl 24)
            val dataStart = offset + CHUNK_HEADER_SIZE

            if (chunkId == "data") {
                val dataEnd = (dataStart + chunkSize)
                    .coerceIn(dataStart.toLong(), wav.size.toLong())
                    .toInt()
                return wav.copyOfRange(dataStart, dataEnd)
            }

            // RIFF 스펙상 청크 데이터는 짝수 바이트로 패딩되므로 홀수 크기면 1바이트 건너뜀
            val nextOffset = dataStart + chunkSize + (chunkSize and 1L)
            // 손상된 chunkSize로 offset이 후퇴하거나 배열 범위를 벗어나면 파싱 중단
            if (nextOffset <= offset || nextOffset > wav.size) return ByteArray(0)
            offset = nextOffset.toInt()
        }
        return ByteArray(0)
    }

    private companion object {
        const val TAG = "CallAudio"

        // 서버 TTS 재생 샘플레이트
        const val PLAYBACK_SAMPLE_RATE_HZ = 44_100

        // RIFF(4) + size(4) + WAVE(4)
        const val RIFF_HEADER_SIZE = 12

        // 청크 id(4) + size(4)
        const val CHUNK_HEADER_SIZE = 8
    }
}

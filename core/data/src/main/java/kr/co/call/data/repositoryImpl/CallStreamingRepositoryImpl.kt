package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.domain.repository.CallStreamingRepository

// TODO: 실제 오디오 스트리밍 연동 전까지의 임시 구현
class CallStreamingRepositoryImpl @Inject constructor() : CallStreamingRepository {

    @Volatile
    private var isMicrophoneEnabled = true

    override suspend fun setMicrophoneEnabled(enabled: Boolean) {
        isMicrophoneEnabled = enabled
        // TODO: 스트리밍 연결 시 상대방에게 마이크 상태 제어 메시지를 전송
    }

    override suspend fun sendAudio(pcmBytes: ByteArray) {
        if (!isMicrophoneEnabled) return

        // TODO: 스트리밍 연결 시 PCM 데이터를 전송
    }

    override suspend fun close() {
        isMicrophoneEnabled = true
    }
}

package kr.co.call.domain.repository

interface CallStreamingRepository {

    // fun open(callId: String): Flow<CallSocketMessage>

    suspend fun setMicrophoneEnabled(enabled: Boolean)

    suspend fun sendAudio(pcmBytes: ByteArray)

    suspend fun close()
}

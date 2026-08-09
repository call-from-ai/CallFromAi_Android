package kr.co.call.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.co.call.domain.model.call.CallStreamingEvent

interface CallStreamingRepository {

    suspend fun setMicrophoneEnabled(enabled: Boolean)

    suspend fun sendAudio(pcmBytes: ByteArray)

    suspend fun close()

    fun connect(wsTicket: String): Flow<CallStreamingEvent>
}

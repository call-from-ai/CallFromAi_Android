package kr.co.call.domain.repository

import kotlinx.coroutines.flow.StateFlow
import kr.co.call.domain.model.call.CallAudioFocusState
import kr.co.call.domain.model.call.CallSessionState

/**
 * 통화 세션
 *
 */
interface CallSessionRepository {

    val sessionState: StateFlow<CallSessionState>

    val audioFocusState: StateFlow<CallAudioFocusState>

    // 통화 세션 시작
    suspend fun startSession(): Boolean

    // 음소거 설정
    suspend fun setMicrophoneEnabled(enabled: Boolean)

    // 스피커 설정
    suspend fun setSpeakerEnabled(enabled: Boolean): Boolean

    suspend fun endSession()
}

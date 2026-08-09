package kr.co.call.impl.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kr.co.call.domain.model.call.CallAudioFocusState
import kr.co.call.domain.model.call.CallSessionState
import kr.co.call.domain.model.call.CallStreamingEvent

/**
 * 통화 중 사용하는 Android 로컬 세션을 관리합니다.
 *
 * 서버나 로컬 저장소의 데이터를 조회하는 Repository가 아니라,
 * 오디오 포커스와 통화 출력 장치 같은 런타임 자원을 제어합니다.
 */
interface CallSessionManager {

    val sessionState: StateFlow<CallSessionState>

    val audioFocusState: StateFlow<CallAudioFocusState>

    // startSession() 성공 이후 통화 소켓에서 오는 이벤트 스트림
    val streamingEvents: Flow<CallStreamingEvent>

    suspend fun startSession(wsTicket: String): Boolean

    suspend fun setMicrophoneEnabled(enabled: Boolean)

    suspend fun setSpeakerEnabled(enabled: Boolean): Boolean

    suspend fun endSession()
}

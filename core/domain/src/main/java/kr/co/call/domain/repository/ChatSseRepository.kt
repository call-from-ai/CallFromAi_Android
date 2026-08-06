package kr.co.call.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kr.co.call.domain.model.chatting.ChatSseEvent

interface ChatSseRepository {

    /**
     * 앱 전역에서 공유되는 SSE 이벤트 스트림.
     * ChatListViewModel / ChatRoomViewModel 에서 구독하며,
     * 채팅방별 이벤트 필터링은 각 ViewModel에서 [chatRoomId]로 처리합니다.
     */
    val sseFlow: SharedFlow<ChatSseEvent>

    /** SSE 연결이 활성화된 상태인지 여부 */
    val isConnected: Boolean

    /** 로그인 성공 시 SSE 연결을 시작 */
    fun connect()

    /** 로그아웃 시 SSE 연결을 종료 */
    fun disconnect()
}

package kr.co.call.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kr.co.call.domain.model.chatting.ChatSseEvent

interface ChatSseRepository {

    /**
     * 앱 전역에서 공유되는 SSE 이벤트 스트림.
     * 채팅방별 구독은 [chatRoomId]로 필터링하여 사용합니다.
     */
    val sseFlow: SharedFlow<ChatSseEvent>

    /** 로그인 성공 시 SSE 연결을 시작 */
    fun connect()

    /** 로그아웃 시 SSE 연결을 종료 */
    fun disconnect()
}

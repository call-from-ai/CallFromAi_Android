package kr.co.call.callfromai.incomingchat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 채팅 알림 상태값을 저장하는 클래스
 * - FCM이 직접 UI를 건드리지 않도록 하기 위한 중간 채팅 상태 저장소입니다.
 * - profileImageUrl 없이 FCM 원본 데이터만 저장하며, AppViewModel에서 헤더 API로 보완합니다.
 */
@Singleton
class IncomingChatStore @Inject constructor() {
    private val _incomingChat = MutableStateFlow<IncomingChat?>(null)
    val incomingChat: StateFlow<IncomingChat?> = _incomingChat.asStateFlow()

    fun show(chat: IncomingChat) {
        _incomingChat.value = chat
    }

    fun clear(chatRoomId: Long) {
        if (_incomingChat.value?.chatRoomId == chatRoomId) {
            _incomingChat.value = null
        }
    }
}
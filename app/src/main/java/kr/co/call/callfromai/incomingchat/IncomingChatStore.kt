package kr.co.call.callfromai.incomingchat

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 채팅 알림을 순서대로 처리하기 위한 큐 저장소입니다.
 *
 * StateFlow 대신 Channel을 사용해 여러 채팅방에서 동시에 알림이 와도 유실 없이 순차 처리합니다.
 * - [show]: 새 알림을 버퍼에 추가합니다.
 * - [incomingChatFlow]: AppViewModel이 collect해 하나씩 꺼내 처리합니다.
 *   collect 람다가 suspend 중이면 Channel이 다음 아이템을 emit하지 않으므로,
 *   AppViewModel에서 현재 다이얼로그가 닫힐 때까지 대기하면 자연스럽게 큐가 됩니다.
 */
@Singleton
class IncomingChatStore @Inject constructor() {
    private val _channel = Channel<IncomingChat>(Channel.BUFFERED)
    val incomingChatFlow: Flow<IncomingChat> = _channel.receiveAsFlow()

    fun show(chat: IncomingChat) {
        _channel.trySend(chat)
    }
}

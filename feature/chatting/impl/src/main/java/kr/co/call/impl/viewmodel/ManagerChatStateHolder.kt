package kr.co.call.impl.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kr.co.call.impl.model.ManagerChatUiItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 매니저 채팅방의 상태를 앱 생명주기 동안 유지하는 싱글톤 홀더입니다.
 *
 * [ManagerChatRoomViewModel]은 네비게이션 진입마다 재생성되지만,
 * 이 홀더는 [Singleton]으로 유지되므로 화면을 나갔다 돌아와도
 * 채팅 내역이 초기화되지 않습니다.
 *
 * @property chatItems 현재까지 쌓인 매니저 채팅 메시지 목록.
 * [ManagerChatRoomViewModel]의 [reduce] 호출 시마다 동기화됩니다.
 * @property isInitialized 최초 진입 여부 플래그.
 * `true`이면 [FirstManagerChatUseCase] 호출을 건너뜁니다.
 */
@Singleton
class ManagerChatStateHolder @Inject constructor() {
    val chatItems = MutableStateFlow<List<ManagerChatUiItem>>(emptyList())
    var isInitialized = false
}

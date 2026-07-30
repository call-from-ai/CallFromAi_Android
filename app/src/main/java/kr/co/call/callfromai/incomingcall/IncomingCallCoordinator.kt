package kr.co.call.callfromai.incomingcall

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.co.call.domain.model.call.IncomingCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 통화 상태값을 저장하는 클래스
 * - FCM이 직접 호출하도록 하지 않기 위한 중간 통화 상태 저장소입니다.
 * - Router에서 모달 또는 IncomingScreen(통화 화면)을 표시하기 위함입니다.
 */
@Singleton
class IncomingCallCoordinator @Inject constructor() {
    private val _incomingCall =
        MutableStateFlow<IncomingCall?>(null)

    val incomingCall: StateFlow<IncomingCall?> =
        _incomingCall.asStateFlow()

    fun show(call: IncomingCall) {
        _incomingCall.value = call
    }

    fun clear(callId: Long) {
        if (_incomingCall.value?.callId == callId) {
            _incomingCall.value = null
        }
    }
}

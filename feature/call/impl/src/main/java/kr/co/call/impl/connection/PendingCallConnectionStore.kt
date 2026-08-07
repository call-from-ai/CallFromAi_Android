package kr.co.call.impl.connection

import javax.inject.Inject
import javax.inject.Singleton
import kr.co.call.domain.model.call.CallConnectionInfo

/**
 * 수락 API에서 발급된 연결 정보를 실제 통화 화면까지 임시로 전달합니다.
 *
 * 짧게 유효한 wsTicket을 영구 저장하지 않고 메모리에 한 건만 보관합니다.
 * [consume]으로 조회한 연결 정보는 다시 사용되지 않도록 즉시 제거됩니다.
 */
@Singleton
class PendingCallConnectionStore @Inject constructor() {

    private var pendingConnection: CallConnectionInfo? = null

    @Synchronized
    fun save(connectionInfo: CallConnectionInfo) {
        pendingConnection = connectionInfo
    }

    @Synchronized
    fun consume(callId: Long): CallConnectionInfo? {
        val connectionInfo = pendingConnection
            ?.takeIf { it.callId == callId }
            ?: return null

        pendingConnection = null
        return connectionInfo
    }

    @Synchronized
    fun clear(callId: Long) {
        if (pendingConnection?.callId == callId) {
            pendingConnection = null
        }
    }
}

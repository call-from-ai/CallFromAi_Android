package kr.co.call.callfromai.incomingcall.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.co.call.domain.model.call.IncomingCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CallStyle 시스템 알림 관리자
 */
@Singleton
class CallNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // 채널 생성
    fun createChannel() {
    }

    fun showIncomingCall(
        call: IncomingCall,
        useFullScreenIntent: Boolean = true,
    ) {
        // 다음 단계에서 구현
    }

    fun showOngoingCall(call: IncomingCall) {
        // 다음 단계에서 구현
    }

    fun cancel(callId: Long) {
        NotificationManagerCompat.from(context)
            .cancel(callId.hashCode())
    }
}

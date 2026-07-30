package kr.co.call.callfromai.incomingcall

import android.app.KeyguardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kr.co.call.callfromai.incomingcall.notification.CallNotificationManager
import kr.co.call.callfromai.lifecycle.AppVisibilityTracker
import kr.co.call.domain.model.call.IncomingCall

/**
 * 상태를 보고 표시 방법을 결정하는 클래스
 * - 모달과 시스템 알림 중 어떤 화면을 띄울지 결정합니다.
 * - 전화를 수신할 때 결정합니다.
 */
@Singleton
class IncomingCallRouter @Inject constructor(
    private val appVisibilityTracker: AppVisibilityTracker,
    private val incomingCallCoordinator: IncomingCallCoordinator,
    private val callNotificationManager: CallNotificationManager,
    @ApplicationContext context: Context,
) {
    private val keyguardManager =
        context.getSystemService(KeyguardManager::class.java)

    fun route(call: IncomingCall) {
        val canShowModal =
            appVisibilityTracker.isMainActivityResumed &&
                !keyguardManager.isKeyguardLocked

        if (canShowModal) {
            incomingCallCoordinator.show(call)
        } else {
            callNotificationManager.showIncomingCall(call)
        }
    }
}

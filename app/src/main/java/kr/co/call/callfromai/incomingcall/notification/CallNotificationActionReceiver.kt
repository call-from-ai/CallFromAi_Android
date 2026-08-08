package kr.co.call.callfromai.incomingcall.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.co.call.common.di.ApplicationScope
import kr.co.call.domain.repository.CallControlRepository
import timber.log.Timber

/**
 * 통화 알림의 통화 거절 동작을 처리합니다.
 * - 앱이 떠있지 않은 상태에서 거절을 하게 되면 앱을 열 필요가 없으므로, 간단하게 거절 api를 호출을 담당합니다.
 */
@AndroidEntryPoint
class CallNotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var callControlRepository: CallControlRepository

    @Inject
    lateinit var callNotificationManager: CallNotificationManager

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != ACTION_DECLINE_CALL) return

        val callId = intent.getLongExtra(EXTRA_CALL_ID, INVALID_CALL_ID)
        if (callId == INVALID_CALL_ID) return

        // 걸려오는 통화를 취소하고 알림을 제거
        callNotificationManager.cancel(callId)
        val pendingResult = goAsync()


        applicationScope.launch {
            try {
                callControlRepository.rejectCall(callId)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (throwable: Throwable) {
                Timber.e(throwable, "착신 통화 거절 실패: callId=%d", callId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val ACTION_DECLINE_CALL =
            "kr.co.call.callfromai.action.DECLINE_CALL"
        private const val EXTRA_CALL_ID =
            "kr.co.call.callfromai.extra.CALL_ID"
        private const val INVALID_CALL_ID = -1L

        fun createDeclineIntent(
            context: Context,
            callId: Long,
        ): Intent =
            Intent(context, CallNotificationActionReceiver::class.java).apply {
                action = ACTION_DECLINE_CALL
                putExtra(EXTRA_CALL_ID, callId)
            }
    }
}

package kr.co.call.callfromai

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.notification.NotificationPermission
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.push.PushDataKeys
import kr.co.call.domain.model.push.PushType
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    /** 다이얼로그 표시 중 Activity 재생성 시 중복 launch 방지 */
    private var notificationPermissionLaunchInFlight = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            NotificationPermission.markRequested(this)
            notificationPermissionLaunchInFlight = false
            Timber.d("POST_NOTIFICATIONS granted=%s", granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPermissionLaunchInFlight =
            savedInstanceState?.getBoolean(STATE_NOTIFICATION_PERMISSION_IN_FLIGHT) == true

        enableEdgeToEdge()
        // 시스템의 창 resize를 끄고, 키보드 대응은 Compose imePadding()에 맡긴다고 되어있었는데 제가 NOTHING->RESIZE로 수정했습니다
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        requestNotificationPermissionIfNeeded()
        handlePushIntent(intent)

        setContent {
            CallFromAiTheme {
                AppScreen(appViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePushIntent(intent)
    }

    private fun handlePushIntent(intent: Intent) {

        val type = PushType.fromDataValue(intent.getStringExtra(PushDataKeys.TYPE))

        if (type == PushType.CHAT) {
            val chatRoomId = intent.getStringExtra(PushDataKeys.CHAT_ROOM_ID)?.toLongOrNull()
                ?: return
            Timber.d("Push 딥링크: CHAT chatRoomId=%d", chatRoomId)
            appViewModel.handleIntent(AppIntent.OnChatPushTapped(chatRoomId))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putBoolean(
                STATE_NOTIFICATION_PERMISSION_IN_FLIGHT,
                notificationPermissionLaunchInFlight,
            )
        }

    /**
     * 알림 권한이 없으면 자동으로 1회만 요청한다.
     * 요청 이력은 시스템 다이얼로그 결과 콜백에서 저장한다.
     * 거부 후 재요청은 설정 등 명시적 사용자 동작에서 처리한다.
     * 거부해도 앱은 계속 사용 가능(배너만 제한)
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (!NotificationPermission.shouldAutoRequest(this)) return
        if (notificationPermissionLaunchInFlight) return
        notificationPermissionLaunchInFlight = true
        notificationPermissionLauncher.launch(NotificationPermission.permission)
    }

    private companion object {
        const val STATE_NOTIFICATION_PERMISSION_IN_FLIGHT =
            "notification_permission_launch_in_flight"
    }
}
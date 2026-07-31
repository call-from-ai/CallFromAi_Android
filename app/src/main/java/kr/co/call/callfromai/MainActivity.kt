package kr.co.call.callfromai

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import kr.co.call.callfromai.notification.NotificationPermission
import kr.co.call.designsystem.theme.CallFromAiTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Timber.d("POST_NOTIFICATIONS granted=%s", granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 시스템의 창 resize를 끄고, 키보드 대응은 Compose imePadding()에 맡긴다
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        requestNotificationPermissionIfNeeded()

        setContent {
            CallFromAiTheme {
                AppScreen(appViewModel)
            }
        }
    }

    /**
     * Android 13+ 에서 알림 권한이 없으면 1회 요청한다
     * 거부해도 앱은 계속 사용 가능(배너만 제한)
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (!NotificationPermission.shouldRequest(this)) return
        notificationPermissionLauncher.launch(NotificationPermission.permission)
    }
}
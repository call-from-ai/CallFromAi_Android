package kr.co.call.callfromai.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Android 13+(Tiramisu) 알림 런타임 권한 헬퍼
 * 그 이하에서는 설치 시 알림 허용으로 취급한다.
 */
object NotificationPermission {

    val permission: String = Manifest.permission.POST_NOTIFICATIONS

    private const val PREFS_NAME = "notification_permission"
    private const val KEY_HAS_REQUESTED = "has_requested_post_notifications"

    fun isGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    /**
     * onCreate 등에서 자동으로 한 번만 요청할지 여부.
     * 이미 허용됐거나, 시스템 다이얼로그 결과까지 받은 적 있으면 false.
     * 거부 후 재요청은 설정 등 명시적 진입점에서 처리한다.
     */
    fun shouldAutoRequest(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        if (isGranted(context)) return false
        return !hasRequested(context)
    }

    /**
     * 권한 다이얼로그 결과 콜백에서 호출한다 (허용/거부 모두)
     */
    fun markRequested(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_REQUESTED, true)
            .apply()
    }

    private fun hasRequested(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_REQUESTED, false)
}

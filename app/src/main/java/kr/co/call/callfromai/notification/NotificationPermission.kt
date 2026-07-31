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

    fun isGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun shouldRequest(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isGranted(context)
}

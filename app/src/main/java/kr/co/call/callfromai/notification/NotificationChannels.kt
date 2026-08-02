package kr.co.call.callfromai.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import kr.co.call.domain.model.push.PushChannels

/**
 * 앱 알림 채널 생성
 */
object NotificationChannels {

    fun ensureAll(context: Context) {
        ensureGeneral(context)
    }

    fun ensureGeneral(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val existing = manager.getNotificationChannel(PushChannels.GENERAL)
        if (existing != null) return

        val channel = NotificationChannel(
            PushChannels.GENERAL,
            "앱 알림",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "채팅/공지/전화 푸시 알림"
            enableVibration(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(channel)
    }
}

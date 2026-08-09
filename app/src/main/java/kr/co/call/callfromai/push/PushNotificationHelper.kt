package kr.co.call.callfromai.push

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.co.call.callfromai.MainActivity
import kr.co.call.callfromai.R
import kr.co.call.domain.model.push.PushChannels
import kr.co.call.domain.model.push.PushDataKeys
import timber.log.Timber

/**
 * 푸시 extras 키 계약
 */
object PushNotificationExtras {
    const val PUSH_TYPE = "push_type"
    const val CHAT_ROOM_ID = "chatRoomId"
    const val CALL_ID = "callId"
    const val CHARACTER_ID = "characterId"
}

/**
 * CHAT / NOTICE / CALL 시스템 배너 (채널 [PushChannels.GENERAL])
 */
object PushNotificationHelper {

    /** 공지 알림 고정 ID (chat/call 대역과 분리) */
    private const val NOTICE_NOTIFICATION_ID = 1

    /** 채팅: [CHAT_ID_BASE, CHAT_ID_BASE + ID_SPAN) */
    private const val CHAT_ID_BASE = 1_000_000

    private const val ID_SPAN = 1_000_000

    fun showChat(
        context: Context,
        title: String,
        body: String,
        chatRoomId: Long,
    ) {
        // 알림 클릭 시 해당 채팅방으로 이동하기 위한 Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(PushDataKeys.TYPE, "CHAT")
            putExtra(PushDataKeys.CHAT_ROOM_ID, chatRoomId.toString())
        }

        // 기존 PendingIntent가 있다면 갱신하고, Android 6.0 이상에서는 불변으로 설정
        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // 채팅방별로 PendingIntent를 구분하여 생성
        val pendingIntent = PendingIntent.getActivity(
            context,
            chatNotificationId(chatRoomId),
            intent,
            pendingFlags,
        )

        // 채팅방별 알림을 표시
        notify(
            context = context,
            notificationId = chatNotificationId(chatRoomId),
            title = title.ifBlank { "새 메시지" },
            body = body,
            priority = NotificationCompat.PRIORITY_HIGH,
            contentIntent = pendingIntent,
        )
    }

    fun showNotice(
        context: Context,
        title: String,
        body: String,
    ) {
        notify(
            context = context,
            notificationId = NOTICE_NOTIFICATION_ID,
            title = title.ifBlank { "알림" },
            body = body,
            priority = NotificationCompat.PRIORITY_HIGH,
        )
    }

    private fun notify(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
        priority: Int,
        contentIntent: PendingIntent? = null,
        category: String? = null,
    ) {
        val builder = NotificationCompat.Builder(context, PushChannels.GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(priority)

        if (contentIntent != null) {
            builder.setContentIntent(contentIntent)
        }
        if (category != null) {
            builder.setCategory(category)
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Timber.w(e, "알림 표시 실패 (권한 확인)")
        }
    }

    private fun chatNotificationId(chatRoomId: Long): Int =
        CHAT_ID_BASE + chatRoomId.mod(ID_SPAN.toLong()).toInt()
}

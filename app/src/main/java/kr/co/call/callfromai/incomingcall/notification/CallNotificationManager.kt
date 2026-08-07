package kr.co.call.callfromai.incomingcall.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kr.co.call.api.RING_TIMEOUT_MILLIS
import kr.co.call.callfromai.incomingcall.IncomingCallActivity
import kr.co.call.designsystem.R
import kr.co.call.domain.model.call.IncomingCall

/**
 * CallStyle 시스템 알림 관리자
 */
@Singleton
class CallNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val imageRequests = ConcurrentHashMap<Long, Disposable>()
    private val activeCallIds = ConcurrentHashMap.newKeySet<Long>()

    // 채널 생성, 벨소리 등 설정
    fun createChannel() {
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE,
        ) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val ringtoneAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            CALL_CHANNEL_ID,
            CALL_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = CALL_CHANNEL_DESCRIPTION
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            enableVibration(true)
            setSound(ringtoneUri, ringtoneAttributes)
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    // incomingScreen 표시하는 함수
    fun showIncomingCall(
        call: IncomingCall,
        useFullScreenIntent: Boolean = true,
    ) {
        if (!canPostNotifications()) return

        activeCallIds += call.callId
        // 프로필이 없을 때 띄울 기본 이미지
        val defaultIcon = IconCompat.createWithResource(
            context,
            R.drawable.img_profile_url_default,
        )

        // 기본 이미지로 착신 알림을 즉시 표시
        notifyIncomingCall(
            call = call,
            callerIcon = defaultIcon,
            useFullScreenIntent = useFullScreenIntent,
        )

        val imageUrl = call.characterImageUrl
            ?.takeIf(String::isNotBlank)
            ?: return

        // 실제 프로필 로딩이 끝나면 같은 알림을 갱신
        imageRequests.remove(call.callId)?.dispose()
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .target(
                onSuccess = { drawable ->
                    imageRequests.remove(call.callId)
                    if (call.callId !in activeCallIds) return@target

                    notifyIncomingCall(
                        call = call,
                        callerIcon = IconCompat.createWithBitmap(drawable.toBitmap()),
                        useFullScreenIntent = useFullScreenIntent,
                    )
                },
                onError = {
                    imageRequests.remove(call.callId)
                },
            )
            .build()

        imageRequests[call.callId] = context.imageLoader.enqueue(request)
    }

    fun cancel(callId: Long) {
        activeCallIds -= callId
        imageRequests.remove(callId)?.dispose()
        notificationManager.cancel(callId.notificationId())
    }

    @SuppressLint("MissingPermission")
    private fun notifyIncomingCall(
        call: IncomingCall,
        callerIcon: IconCompat,
        useFullScreenIntent: Boolean,
    ) {
        if (call.callId !in activeCallIds || !canPostNotifications()) return

        val caller = Person.Builder()
            .setName(call.characterName)
            .setIcon(callerIcon)
            .setImportant(true)
            .build()
        val incomingCallIntent = createIncomingCallIntent(
            call = call,
            autoAccept = false,
            requestCode = call.callId.requestCode(CONTENT_REQUEST_OFFSET),
        )
        val answerIntent = createIncomingCallIntent(
            call = call,
            autoAccept = true,
            requestCode = call.callId.requestCode(ANSWER_REQUEST_OFFSET),
        )
        val declineIntent = PendingIntent.getBroadcast(
            context,
            call.callId.requestCode(DECLINE_REQUEST_OFFSET),
            CallNotificationActionReceiver.createDeclineIntent(
                context = context,
                callId = call.callId,
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, CALL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_call_accept_icon)
            .setContentTitle(call.characterName)
            .setContentText(INCOMING_CALL_TEXT)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setTimeoutAfter(RING_TIMEOUT_MILLIS)
            .setContentIntent(incomingCallIntent)
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    caller,
                    declineIntent,
                    answerIntent,
                ),
            )

        if (useFullScreenIntent) {
            builder.setFullScreenIntent(incomingCallIntent, true)
        }

        notificationManager.notify(
            call.callId.notificationId(),
            builder.build(),
        )
    }

    private fun createIncomingCallIntent(
        call: IncomingCall,
        autoAccept: Boolean,
        requestCode: Int,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            IncomingCallActivity.createIntent(
                context = context,
                call = call,
                autoAccept = autoAccept,
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun Long.notificationId(): Int = hashCode()

    private fun Long.requestCode(offset: Int): Int = 31 * hashCode() + offset

    private companion object {
        const val CALL_CHANNEL_ID = "incoming_calls"
        const val CALL_CHANNEL_NAME = "수신 전화"
        const val CALL_CHANNEL_DESCRIPTION = "AI 캐릭터의 수신 전화를 표시합니다."
        const val INCOMING_CALL_TEXT = "전화가 왔어요"

        const val CONTENT_REQUEST_OFFSET = 1
        const val ANSWER_REQUEST_OFFSET = 2
        const val DECLINE_REQUEST_OFFSET = 3
    }
}

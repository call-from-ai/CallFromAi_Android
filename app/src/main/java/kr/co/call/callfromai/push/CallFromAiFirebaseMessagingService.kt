package kr.co.call.callfromai.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.co.call.common.di.ApplicationScope
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.push.PushPayload
import kr.co.call.domain.model.push.PushPayloadParser
import kr.co.call.domain.repository.ChatSseRepository
import timber.log.Timber

/**
 * FCM 수신 진입점 (type 분기)
 */
@AndroidEntryPoint
class CallFromAiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenManager: PushTokenManager

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var chatSseRepository: ChatSseRepository

    override fun onNewToken(token: String) {
        Timber.d("FCM onNewToken (length=%d)", token.length)
        applicationScope.launch {
            if (tokenDataStore.getTokens().accessToken.isNullOrBlank()) {
                Timber.d("FCM onNewToken: 미로그인 -> 서버 등록 스킵")
                return@launch
            }
            pushTokenManager.registerCurrentDevice()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]

        if (type == "CHAT") {
            Timber.d(
                """
            ========== FCM CHAT ==========
            messageId: ${message.messageId}
            chatRoomId: ${message.data["chatRoomId"]}
            title: ${message.notification?.title}
            body: ${message.notification?.body}
            data: ${message.data}
            ==============================
            """.trimIndent()
            )
        }


        val payload = PushPayloadParser.parse(
            data = message.data,
            notificationTitle = message.notification?.title,
            notificationBody = message.notification?.body,
        )
        when (payload) {
            // Chat 알림 — SSE 연결 중(채팅 화면)이면 이미 실시간 수신 중이므로 스킵
            is PushPayload.Chat -> {
                Timber.d("FCM CHAT roomId=%s", payload.chatRoomId)
                if (chatSseRepository.isConnected) {
                    Timber.d("FCM CHAT 스킵: SSE 연결 중(사용자가 채팅 관련 화면에 있음)")
                    return
                }
                PushNotificationHelper.showChat(
                    context = this,
                    title = payload.title,
                    body = payload.body,
                    chatRoomId = payload.chatRoomId,
                )
            }
            // Notice 알림
            is PushPayload.Notice -> {
                Timber.d("FCM NOTICE title=%s", payload.title)
                PushNotificationHelper.showNotice(
                    context = this,
                    title = payload.title,
                    body = payload.body,
                )
            }
            // Call 알림
            is PushPayload.Call -> {
                Timber.d(
                    "FCM CALL callId=%s characterId=%s name=%s",
                    payload.callId,
                    payload.characterId,
                    payload.characterName,
                )
                PushNotificationHelper.showCall(
                    context = this,
                    characterName = payload.characterName,
                    callId = payload.callId,
                )
            }
            null -> Timber.w("Unknown push data=%s", message.data)
        }
    }
}

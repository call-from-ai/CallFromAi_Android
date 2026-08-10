package kr.co.call.callfromai.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.co.call.callfromai.incomingcall.IncomingCallRouter
import kr.co.call.callfromai.incomingchat.IncomingChat
import kr.co.call.callfromai.incomingchat.IncomingChatStore
import kr.co.call.common.di.ApplicationScope
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.push.PushPayload
import kr.co.call.domain.model.push.PushPayloadParser
import kr.co.call.domain.model.push.toIncomingCall
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
    lateinit var incomingCallRouter: IncomingCallRouter

    @Inject
    lateinit var incomingChatStore: IncomingChatStore

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
        Timber.d(
            """
        ========== FCM RECEIVED ==========
        messageId: ${message.messageId}
        from: ${message.from}
        sentTime: ${message.sentTime}
        priority: ${message.priority}
        originalPriority: ${message.originalPriority}
        notification:
          title=${message.notification?.title}
          body=${message.notification?.body}
        data: ${message.data}
        ==================================
        """.trimIndent()
        )

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
            // Chat 알림 — SSE 연결 중(채팅 화면)이면 이미 실시간 수신 중이므로 스킵 -> 서버에 로직이 반영되어 있음
            // 그에 따라 주석 처리
            is PushPayload.Chat -> {
                Timber.d("FCM CHAT roomId=%s", payload.chatRoomId)
//                if (chatSseRepository.isConnected) {
//                    Timber.d("FCM CHAT 스킵: SSE 연결 중(사용자가 채팅 관련 화면에 있음)")
//                    return
//                }
                // notification 필드가 있는 CHAT FCM은 백그라운드에서 Android가 자동 처리하므로
                // onMessageReceived는 포그라운드에서만 호출됨 → 항상 인앱 다이얼로그로 표시
                // (profileImageUrl은 AppViewModel에서 헤더 API 호출 후 보완)
                incomingChatStore.show(
                    IncomingChat(
                        chatRoomId = payload.chatRoomId,
                        characterName = payload.title,
                        message = payload.body,
                    ),
                )
            }
            // Notice 알림
            is PushPayload.Notice -> {
                Timber.d("FCM NOTICE title=%s", payload.title)
                PushNotificationHelper.showNotice(
                    context = this@CallFromAiFirebaseMessagingService,
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
                incomingCallRouter.route(payload.toIncomingCall())
            }
            null -> {
                Timber.w("Unknown push data=%s", message.data)
                // type 없이 notification 필드만 있는 경우 (예: 콘솔 테스트) 폴백 표시
                val title = message.notification?.title
                val body = message.notification?.body
                if (!title.isNullOrBlank() || !body.isNullOrBlank()) {
                    PushNotificationHelper.showNotice(
                        context = this@CallFromAiFirebaseMessagingService,
                        title = title.orEmpty(),
                        body = body.orEmpty(),
                    )
                }
            }
        }
    }
}

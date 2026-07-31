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
import timber.log.Timber

@AndroidEntryPoint
class CallFromAiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenManager: PushTokenManager

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    /**
     * FCM 등록 토큰이 갱신될 때 호출된다.
     * JWT가 있을 때만 서버에 재등록한다
     */
    override fun onNewToken(token: String) {
        Timber.d("FCM onNewToken (length=%d)", token.length)
        applicationScope.launch {
            if (tokenDataStore.getAccessToken().isNullOrBlank()) {
                Timber.d("FCM onNewToken: 미로그인 -> 서버 등록 스킵")
                return@launch
            }
            pushTokenManager.registerCurrentDevice()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val payload = PushPayloadParser.parse(
            data = message.data,
            notificationTitle = message.notification?.title,
            notificationBody = message.notification?.body,
        )
        when (payload) {
            is PushPayload.Chat -> { /* 배너 + PendingIntent */ }
            is PushPayload.Call -> { /* 착신 UI */ }
            is PushPayload.Notice -> { /* 배너 */ }
            null -> Timber.w("Unknown push: %s", message.data)
        }
    }
}

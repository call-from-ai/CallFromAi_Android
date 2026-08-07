package kr.co.call.network.websocket

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kr.co.call.network.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber

/**
 * 통화 WebSocket 생성 책임을 OkHttp 구현으로 감쌉니다.
 */
@Singleton
class CallWebSocketClient @Inject constructor(
    @param:Named("callWebSocketClient")
    private val okHttpClient: OkHttpClient,
) {

    fun open(
        wsTicket: String,
        listener: WebSocketListener,
    ): WebSocket {
        val baseUrl = BuildConfig.BASE_URL.toHttpUrl()
        val webSocketUrl = baseUrl.newBuilder()
            .scheme(if (baseUrl.isHttps) "https" else "http")
            .encodedPath("/ws/call")
            .query(null)
            .addQueryParameter("ticket", wsTicket)
            .build()
        val request = Request.Builder()
            .url(webSocketUrl)
            .build()

        Timber.tag("CallSocket").d("WebSocket 요청 URL: %s", webSocketUrl)
        return okHttpClient.newWebSocket(request, listener)
    }
}

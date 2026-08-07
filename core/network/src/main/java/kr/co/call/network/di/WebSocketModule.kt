package kr.co.call.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.Authenticator
import okhttp3.OkHttpClient


@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    @Named("callWebSocketClient")
    fun provideCallWebSocketOkHttpClient(
        restOkHttpClient: OkHttpClient,
    ): OkHttpClient {
        return restOkHttpClient.newBuilder()
            .apply {
                // REST 인증과 로깅 없이 WebSocket 연결 자원만 공유
                interceptors().clear()
                networkInterceptors().clear()
            }
            .authenticator(Authenticator.NONE)
            .build()
    }
}

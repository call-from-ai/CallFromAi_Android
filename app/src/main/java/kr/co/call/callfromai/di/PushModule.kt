package kr.co.call.callfromai.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kr.co.call.callfromai.push.FirebaseFcmTokenProvider
import kr.co.call.domain.push.FcmTokenProvider

/**
 * FCM 토큰 제공자를 Firebase 구현으로 연결
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PushModule {
    @Binds
    @Singleton
    abstract fun bindFcmTokenProvider(
        impl: FirebaseFcmTokenProvider,
    ): FcmTokenProvider
}
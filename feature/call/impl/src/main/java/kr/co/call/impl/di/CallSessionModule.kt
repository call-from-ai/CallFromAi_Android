package kr.co.call.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kr.co.call.impl.session.AndroidCallSessionManager
import kr.co.call.impl.session.CallSessionManager

@Module
@InstallIn(SingletonComponent::class)
abstract class CallSessionModule {

    @Binds
    @Singleton
    abstract fun bindCallSessionManager(
        implementation: AndroidCallSessionManager,
    ): CallSessionManager
}

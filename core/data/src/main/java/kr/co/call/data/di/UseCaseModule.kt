package kr.co.call.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.domain.usecase.chatting.FirstManagerChatUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideFirstManagerChatUseCase(): FirstManagerChatUseCase {
        return FirstManagerChatUseCase()
    }
}

package kr.co.call.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.domain.usecase.chatting.FirstManagerChatUseCase
import kr.co.call.domain.usecase.chatting.WantToContactManagerUseCase
import kr.co.call.domain.usecase.chatting.WantToGetCallScheduleUseCase
import kr.co.call.domain.usecase.chatting.WantToUpdatePartnerInfoUseCase
import kr.co.call.domain.usecase.chatting.WantToUpdateRecordUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ======== 매니저 채팅 관련 =========
    @Provides
    @Singleton
    fun provideFirstManagerChatUseCase(): FirstManagerChatUseCase {
        return FirstManagerChatUseCase()
    }

    @Provides
    @Singleton
    fun provideWantToContactManagerUseCase(): WantToContactManagerUseCase {
        return WantToContactManagerUseCase()
    }

    @Provides
    @Singleton
    fun provideWantToGetCallScheduleUseCase(): WantToGetCallScheduleUseCase {
        return WantToGetCallScheduleUseCase()
    }

    @Provides
    @Singleton
    fun provideWantToUpdatePartnerInfoUseCase(): WantToUpdatePartnerInfoUseCase {
        return WantToUpdatePartnerInfoUseCase()
    }

    @Provides
    @Singleton
    fun provideWantToUpdateRecordUseCase(): WantToUpdateRecordUseCase {
        return WantToUpdateRecordUseCase()
    }

}

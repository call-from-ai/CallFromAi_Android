package kr.co.call.data.di

import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kr.co.call.data.repositoryImpl.CallRecordMockRepository
import kr.co.call.data.repositoryImpl.ChatRepositoryImpl
import kr.co.call.data.repositoryImpl.HomeMockRepository
import kr.co.call.data.repositoryImpl.MyPageRepositoryImpl
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.domain.repository.MyPageRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeMockRepository: HomeMockRepository,
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCallRecordRepository(
        callRecordMockRepository: CallRecordMockRepository,
    ): CallRecordRepository

    @Binds
    abstract fun bindMyPageRepository(
        impl: MyPageRepositoryImpl
    ): MyPageRepository
}

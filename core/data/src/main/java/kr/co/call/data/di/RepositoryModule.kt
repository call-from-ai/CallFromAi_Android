package kr.co.call.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.data.repositoryImpl.FaqRepositoryImpl
import kr.co.call.data.repositoryImpl.ChatRepositoryImpl
import kr.co.call.domain.repository.ChatRepository
import javax.inject.Singleton
import kr.co.call.data.repositoryImpl.HomeRepositoryImpl
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.data.repositoryImpl.MyPageRepositoryImpl
import kr.co.call.domain.repository.FaqRepository
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
        homeRepositoryImpl: HomeRepositoryImpl,
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(
        impl: MyPageRepositoryImpl
    ): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindFaqRepository(
        impl: FaqRepositoryImpl
    ): FaqRepository
}
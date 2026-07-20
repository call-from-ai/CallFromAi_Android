package kr.co.call.data.di

import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.data.repositoryImpl.ChatRepositoryImpl
import kr.co.call.domain.repository.ChatRepository
import javax.inject.Singleton
import kr.co.call.data.repositoryImpl.HomeRepositoryImpl
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.data.repositoryImpl.AICharacterRepositoryImpl
import kr.co.call.data.repositoryImpl.MyPageRepositoryImpl
import kr.co.call.domain.repository.AICharacterRepository
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
    abstract fun bindMyPageRepository(
        impl: MyPageRepositoryImpl
    ): MyPageRepository

    @Binds
    abstract fun bindCharacterRepository(
        impl: AICharacterRepositoryImpl
    ): AICharacterRepository
}
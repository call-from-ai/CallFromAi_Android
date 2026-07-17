package kr.co.call.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.data.repositoryImpl.FaqRepositoryImpl
import kr.co.call.data.repositoryImpl.MyPageRepositoryImpl
import kr.co.call.domain.repository.FaqRepository
import kr.co.call.domain.repository.MyPageRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMyPageRepository(
        impl: MyPageRepositoryImpl
    ): MyPageRepository

    @Binds
    abstract fun bindFaqRepository(
        impl: FaqRepositoryImpl
    ): FaqRepository
}
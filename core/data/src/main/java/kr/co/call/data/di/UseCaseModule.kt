package kr.co.call.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.domain.usecase.home.HomeUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideHomeUseCase(
        homeRepository: HomeRepository,
    ): HomeUseCase = HomeUseCase(homeRepository)
}

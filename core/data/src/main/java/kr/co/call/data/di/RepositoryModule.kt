package kr.co.call.data.di

import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.data.repositoryImpl.AgreementRepositoryImpl
import kr.co.call.data.repositoryImpl.AICharacterRepositoryImpl
import kr.co.call.data.repositoryImpl.CallRecordRepositoryImpl
import kr.co.call.data.repositoryImpl.FaqRepositoryImpl
import kr.co.call.data.repositoryImpl.ChatRepositoryImpl
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.domain.repository.ChatRepository
import javax.inject.Singleton
import kr.co.call.data.repositoryImpl.HomeRepositoryImpl
import kr.co.call.data.repositoryImpl.LoginRepositoryImpl
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.domain.repository.FaqRepository
import kr.co.call.data.repositoryImpl.MyPageRepositoryImpl
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.data.repositoryImpl.CallControlRepositoryImpl
import kr.co.call.data.repositoryImpl.AndroidCallSessionRepository
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.repository.CallSessionRepository
import kr.co.call.data.repositoryImpl.CallStreamingRepositoryImpl
import kr.co.call.domain.repository.CallStreamingRepository


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
    abstract fun bindCallRecordRepository(
        callRecordRepositoryImpl: CallRecordRepositoryImpl,
    ): CallRecordRepository

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(
        impl: MyPageRepositoryImpl
    ): MyPageRepository
    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl,
    ): LoginRepository
    @Binds
    @Singleton
    abstract fun bindAgreementRepository(
        agreementRepositoryImpl: AgreementRepositoryImpl,
    ): AgreementRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: AICharacterRepositoryImpl
    ): AICharacterRepository

    @Binds
    @Singleton
    abstract fun bindFaqRepository(
        impl: FaqRepositoryImpl
    ): FaqRepository

    @Binds
    @Singleton
    abstract fun bindCallControlRepository(
        impl: CallControlRepositoryImpl
    ): CallControlRepository

    @Binds
    @Singleton
    abstract fun bindCallStreamingRepository(
        impl: CallStreamingRepositoryImpl
    ): CallStreamingRepository

    @Binds
    @Singleton
    abstract fun bindCallSessionRepository(
        impl: AndroidCallSessionRepository,
    ): CallSessionRepository
}

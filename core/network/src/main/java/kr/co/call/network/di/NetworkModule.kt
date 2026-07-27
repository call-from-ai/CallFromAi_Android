package kr.co.call.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Network 공통 의존성.
 *
 * [kr.co.call.network.util.ErrorResponseParser] 는 생성자 @Inject 로 제공된다.
 * (같은 Gson 인스턴스를 쓰도록 [provideGson] 을 공유)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }
}

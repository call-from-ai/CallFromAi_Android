package kr.co.call.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kr.co.call.network.BuildConfig
import kr.co.call.network.api.CharacterApi
import kr.co.call.network.api.HomeApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object HomeNetworkModule {

    private const val OKHTTP_LOG_TAG = "HomeOkHttp"

    @Provides
    @Singleton
    @Named("homeOkHttpClient")
    fun provideHomeOkHttpClient(
        okHttpClient: OkHttpClient,
    ): OkHttpClient =
        okHttpClient.newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor { message ->
                    Timber.tag(OKHTTP_LOG_TAG).d(message)
                }.apply {
                    redactHeader("Authorization")
                    redactHeader("Cookie")
                    redactHeader("Set-Cookie")
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                },
            )
            .build()

    @Provides
    @Singleton
    @Named("homeRetrofit")
    fun provideHomeRetrofit(
        retrofit: Retrofit,
        @Named("homeOkHttpClient")
        homeOkHttpClient: OkHttpClient,
    ): Retrofit =
        retrofit.newBuilder()
            .client(homeOkHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideHomeApi(
        @Named("homeRetrofit")
        retrofit: Retrofit,
    ): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideCharacterApi(
        @Named("homeRetrofit")
        retrofit: Retrofit,
    ): CharacterApi =
        retrofit.create(CharacterApi::class.java)
}

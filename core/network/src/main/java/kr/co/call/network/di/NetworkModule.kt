package kr.co.call.network.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.api.LoginApi
import kr.co.call.network.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().create()
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClien(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        val loggingInterceptor= HttpLoggingInterceptor().apply{
            level=HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.lovecall.example.com/api/v1/")
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApi(
        retrofit: Retrofit,
    ): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAgreementApi(retrofit: Retrofit): AgreementApi {
        return retrofit.create(AgreementApi::class.java)
    }
}

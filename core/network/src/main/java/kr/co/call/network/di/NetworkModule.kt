package kr.co.call.network.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.api.LoginApi
import kr.co.call.network.api.TokenReissueApi
import kr.co.call.network.interceptor.AuthInterceptor
import kr.co.call.network.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
    서버에서 받은 JSON 응답을 Kotlin 객체로 변환하고
    Kotlin 객체를 JSON 요청 본문으로 변환하는 Converter를 제공
     */
    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().create()
        )
    }

    //일반 API 요청에 사용하는 OkHttpClient를 제공
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor= HttpLoggingInterceptor().apply{
            level=HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    //일반 API 요청에 사용할 Retrofit 객체를 제공
    @Provides
    @Singleton
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.lovecall.example.com/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    //토큰 재발급api 전용 OkHttpClient를 제공
    @Provides
    @Singleton
    @Named("reissueOkHttpClient")
    fun provideReissueOkHttpClient(): OkHttpClient{
        val loggingInterceptor= HttpLoggingInterceptor().apply{
            level= HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    //토큰 재발급 API 전용 Retrofit 객체를 제공
    @Provides
    @Singleton
    @Named("reissueRetrofit")
    fun provideReissueRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @Named("reissueOkHttpClient")
        reissueOkHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.lovecall.example.com/api/v1/")
            .client(reissueOkHttpClient)
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

    @Provides
    @Singleton
    fun provideTokenReissueApi(
        @Named("reissueRetrofit")
        retrofit: Retrofit,
    ): TokenReissueApi{
        return retrofit.create(TokenReissueApi::class.java)
    }
}

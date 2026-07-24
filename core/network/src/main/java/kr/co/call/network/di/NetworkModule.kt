package kr.co.call.network.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.network.BuildConfig
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
    /*
     * 일반 API 통신 내용을 확인하기 위한 로깅 인터셉터이다.
     * 인증 정보가 로그에 노출되지 않도록 민감한 헤더를 마스킹한다.
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")

            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /*
     * 토큰 재발급 요청에는 Access Token과 Refresh Token이
     * Request Body에 들어가므로 BODY 내용을 출력하지 않는다.
     */
    private fun createReissueLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")

            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    //일반 API 요청에 사용하는 OkHttpClient를 제공
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(createLoggingInterceptor())
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
        return OkHttpClient.Builder()
            .addInterceptor(createReissueLoggingInterceptor())
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

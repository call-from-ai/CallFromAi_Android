package kr.co.call.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.call.network.BuildConfig
import kr.co.call.network.api.AICharacterApi
import kr.co.call.network.api.AgreementApi
import kr.co.call.network.api.CallApi
import kr.co.call.network.api.CharacterApi
import kr.co.call.network.api.HomeApi
import kr.co.call.network.api.LoginApi
import kr.co.call.network.api.PushTokenApi
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.api.PresetImageApi
import kr.co.call.network.api.RelationshipApi
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
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
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
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }

    //일반 API 요청에 사용할 Retrofit 객체를 제공
    @Provides
    @Singleton
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    //토큰 재발급api 전용 OkHttpClient를 제공
    @Provides
    @Singleton
    @Named("reissueOkHttpClient")
    fun provideReissueOkHttpClient(): OkHttpClient {
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
            .baseUrl(BuildConfig.BASE_URL)
            .client(reissueOkHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApi(
        retrofit: Retrofit,
    ): LoginApi =
        retrofit.create(LoginApi::class.java)

    @Provides
    @Singleton
    fun provideAgreementApi(
        retrofit: Retrofit,
    ): AgreementApi =
        retrofit.create(AgreementApi::class.java)

    @Provides
    @Singleton
    fun provideHomeApi(
        retrofit: Retrofit,
    ): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideCharacterApi(
        retrofit: Retrofit,
    ): CharacterApi =
        retrofit.create(CharacterApi::class.java)

    @Provides
    @Singleton
    fun provideCallApi(retrofit: Retrofit): CallApi {
        return retrofit.create(CallApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenReissueApi(
        @Named("reissueRetrofit")
        retrofit: Retrofit,
    ): TokenReissueApi{
        return retrofit.create(TokenReissueApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyPageApi(
        retrofit: Retrofit,
    ): MyPageApi{
        return retrofit.create(MyPageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRelationshipApi(
        retrofit: Retrofit,
    ): RelationshipApi =
        retrofit.create(RelationshipApi::class.java)

    @Provides
    @Singleton
    fun provideAICharacterApi(
        retrofit: Retrofit,
    ): AICharacterApi{
        return retrofit.create(AICharacterApi::class.java)
    }

    //프로필 사진 이미지 받아옴
    @Provides
    @Singleton
    fun providePresetImageApi(
        retrofit: Retrofit,
    ): PresetImageApi =
        retrofit.create(PresetImageApi::class.java)

    @Provides
    @Singleton
    fun providePushTokenApi(
        retrofit: Retrofit,
    ): PushTokenApi =
        retrofit.create(PushTokenApi::class.java)
}

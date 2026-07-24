package kr.co.call.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 인증 토큰을 저장할 Preferences DataStore를 생성한다.
 * 같은 파일을 앱 전체에서 하나만 사용하도록 Context 확장 프로퍼티로 선언한다.
 */
private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name="call_from_ai_token_prefs",
    corruptionHandler = ReplaceFileCorruptionHandler{
        emptyPreferences()
    },
)

/**
 * TokenDataStore에서 사용할 DataStore 객체를 Hilt로 제공하는 모듈
 * 앱 전체에서 동일한 DataStore 인스턴스를 사용할 수 있도록 Singleton으로 관리한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule{
    @Provides
    @Singleton
    fun providerPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =context.tokenDataStore
}
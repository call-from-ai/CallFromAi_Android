package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.co.call.common.di.ApplicationScope
import timber.log.Timber

/**
 * DataStore에 저장된 Access Token과 Refresh Token을 함께 표현한다.
 * TokenAuthenticator에서 두 토큰을 한 번에 읽을 때 사용한다.
 */
data class StoredTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)

/**
 * 서버에서 발급한 Access Token과 Refresh Token을 DataStore에 저장한다.
 * AuthInterceptor가 빠르게 읽을 수 있도록 같은 토큰을 메모리에도 캐시한다.
 */
@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    /**
     * OkHttp Interceptor에서 DataStore를 매번 읽지 않도록
     * 현재 토큰 값을 메모리에 보관한다.
     */
    private val cachedTokens = AtomicReference(
        StoredTokens(),
    )

    /**
     * DataStore 파일을 읽는 과정에서 IOException이 발생하면
     * 앱을 종료하지 않고 빈 Preferences를 전달해 비로그인 상태로 처리한다.
     */
    private val safeData: Flow<Preferences> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                Timber.e(
                    error,
                    "토큰 DataStore 읽기 실패",
                )

                emit(emptyPreferences())
            } else {
                throw error
            }
        }

    init {
        /**
         * 앱이 실행되는 동안 DataStore의 변경 내용을 계속 관찰한다.
         * 토큰이 저장되거나 삭제되면 메모리 캐시에도 같은 값을 반영한다.
         */
        applicationScope.launch {
            safeData.collect { preferences ->
                cachedTokens.set(
                    preferences.toStoredTokens(),
                )
            }
        }
    }

    /**
     * AuthInterceptor가 사용할 Access Token을 메모리에서 가져온다.
     * 디스크를 읽지 않으므로 OkHttp 네트워크 스레드를 차단하지 않는다.
     */
    fun getCachedAccessToken(): String? {
        return cachedTokens
            .get()
            .accessToken
            ?.takeIf { it.isNotBlank() }
    }

    /**
     * DataStore에 저장된 Access Token과 Refresh Token을 한 번에 읽는다.
     * 읽은 토큰은 이후 빠르게 사용할 수 있도록 캐시에도 반영한다.
     */
    suspend fun getStoredTokens(): StoredTokens {
        val tokens = safeData
            .first()
            .toStoredTokens()

        cachedTokens.set(tokens)

        return tokens
    }

    /**
     * API 인증 헤더에 사용할 Access Token을 가져온다.
     * 자동 로그인 여부를 확인할 때도 이 함수를 사용한다.
     */
    suspend fun getAccessToken(): String? {
        return getStoredTokens().accessToken
    }

    /**
     * Access Token 재발급에 필요한 Refresh Token을 가져온다.
     */
    suspend fun getRefreshToken(): String? {
        return getStoredTokens().refreshToken
    }

    /**
     * 로그인 또는 토큰 재발급에 성공했을 때 새 토큰을 저장한다.
     * 빈 문자열이 인증 토큰으로 저장되지 않도록 저장 전에 검증한다.
     */
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        require(accessToken.isNotBlank()) {
            "Access Token은 비어 있을 수 없습니다."
        }

        require(refreshToken.isNotBlank()) {
            "Refresh Token은 비어 있을 수 없습니다."
        }

        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }

        cachedTokens.set(
            StoredTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            ),
        )
    }

    /**
     * Refresh Token까지 만료되거나 로그아웃한 경우 토큰을 삭제한다.
     * DataStore와 메모리 캐시를 모두 초기화해 자동 로그인을 방지한다.
     */
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }

        cachedTokens.set(
            StoredTokens(),
        )
    }

    /**
     * Preferences에 저장된 값을 StoredTokens 객체로 변환한다.
     */
    private fun Preferences.toStoredTokens(): StoredTokens {
        return StoredTokens(
            accessToken = this[ACCESS_TOKEN],
            refreshToken = this[REFRESH_TOKEN],
        )
    }

    private companion object {
        val ACCESS_TOKEN =
            stringPreferencesKey("access_token")

        val REFRESH_TOKEN =
            stringPreferencesKey("refresh_token")
    }
}
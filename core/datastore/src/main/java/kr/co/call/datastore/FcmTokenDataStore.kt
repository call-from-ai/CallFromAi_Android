package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * FCM 기기 토큰 로컬 저장
 *
 * - 서버 등록(/push-tokens) 성공 후 저장
 * - 로그아웃 시 읽어 DELETE 후 clear
 * - JWT([TokenDataStore])와 키만 다르고 같은 Preferences 파일을 공유한다.
 */
@Singleton
class FcmTokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                Timber.e(error, "FCM 토큰 DataStore 읽기 실패")
                emit(emptyPreferences())
            } else {
                throw error
            }
        }

    /** 저장된 FCM 토큰 스트림. 없으면 null */
    val tokenFlow: Flow<String?> = safeData.map { preferences ->
        preferences[FCM_TOKEN]?.takeIf { it.isNotBlank() }
    }

    suspend fun getToken(): String? =
        safeData.first()[FCM_TOKEN]?.takeIf { it.isNotBlank() }

    /**
     * FCM 토큰 저장. 빈 문자열은 거부한다.
     */
    suspend fun saveToken(token: String) {
        require(token.isNotBlank()) { "FCM 토큰은 비어 있을 수 없습니다." }
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }
    }

    /**
     * 로그아웃/토큰 무효화 시 로컬 FCM 토큰 제거
     * JWT clear 와 별개로 호출한다(호출 순서는 상위 플로우 책임).
     */
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(FCM_TOKEN)
        }
    }

    private companion object {
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
    }
}

package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버에서 발급받은 Access Token과 Refresh Token을 로컬에 저장하는 클래스
 * API 인증, 자동 로그인 확인, 토큰 재발급 과정에서 저장된 토큰을 제공한다.
 */
@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
){
    // API 요청 헤더에 사용할 Access Token을 가져온다.
    suspend fun getAccessToken():String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }

    // Access Token 재발급에 사용할 Refresh Token을 가져온다.
    suspend fun getRefreshToken():String?{
        return dataStore.data.first()[REFRESH_TOKEN]
    }

    /**
     * 로그인 또는 토큰 재발급에 성공했을 때
     * 새로운 Access Token과 Refresh Token을 함께 저장한다.
     */
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ){
        dataStore.edit {preferences ->
            preferences[ACCESS_TOKEN]=accessToken
            preferences[REFRESH_TOKEN]=refreshToken
        }
    }

    /**
     * Refresh Token까지 만료됐거나 로그아웃하는 경우
     * 기기에 저장된 인증 토큰을 모두 삭제한다.
     */
    suspend fun clearTokens(){
        dataStore.edit{preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
    private companion object {
        val ACCESS_TOKEN=stringPreferencesKey("access_token")
        val REFRESH_TOKEN=stringPreferencesKey("refresh_token")
    }
}
package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/**
 * DataStore에 저장된 Access Token과 Refresh Token을 함께 표현한다.
 * TokenAuthenticator에서 두 토큰을 한 번에 읽을 때 사용한다.
 */
data class StoredTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val needsOnboarding: Boolean=false,
    val needsTermsAgreement: Boolean=false,
)

/** 서버에서 발급한 Access Token과 Refresh Token을 DataStore에서 관리한다. */
@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun getTokens(): StoredTokens {
        return dataStore.data.first().toStoredTokens()
    }

    //로그인 응답 전체 저장
    suspend fun setLoginSession(
        accessToken: String,
        refreshToken: String,
        needsOnboarding: Boolean,
        needsTermsAgreement: Boolean,
    ){
        require(accessToken.isNotBlank()){
            "Access Token은 비어 있을 수 없습니다."
        }
        require(refreshToken.isNotBlank()){
            "Refresh Token은 비어 있을 수 없습니다."
        }
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[NEEDS_ONBOARDING] = needsOnboarding
            preferences[NEEDS_TERMS_AGREEMENT] = needsTermsAgreement
        }
    }
    //토큰 재발급 시 토큰만 갱신
    suspend fun setTokens(
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
    }
    //약관 완료 후 needsTermsAgreement값 변경
    suspend fun setNeedsTermsAgreement(needsTermsAgreement: Boolean) {
        dataStore.edit { preferences ->
            preferences[NEEDS_TERMS_AGREEMENT] = needsTermsAgreement
        }
    }

    /** 로그아웃하거나 세션이 만료됐을 때 저장된 토큰을 삭제한다. */
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(NEEDS_ONBOARDING)
            preferences.remove(NEEDS_TERMS_AGREEMENT)
        }
    }

    /**
     * Preferences에 저장된 값을 StoredTokens 객체로 변환한다.
     */
    private fun Preferences.toStoredTokens(): StoredTokens {
        return StoredTokens(
            accessToken = this[ACCESS_TOKEN]?.takeIf { it.isNotBlank() },
            refreshToken = this[REFRESH_TOKEN]?.takeIf { it.isNotBlank() },
            needsOnboarding=this[NEEDS_ONBOARDING] ?:false,
            needsTermsAgreement = this[NEEDS_TERMS_AGREEMENT] ?:false,
        )
    }

    private companion object {
        val ACCESS_TOKEN =
            stringPreferencesKey("access_token")

        val REFRESH_TOKEN =
            stringPreferencesKey("refresh_token")

        val NEEDS_ONBOARDING=
            booleanPreferencesKey("needs_onboarding")

        val NEEDS_TERMS_AGREEMENT=
            booleanPreferencesKey("needs_terms_agreement")
    }
}

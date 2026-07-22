package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
){
    val accessToken: Flow<String?> =
            dataStore.data.map {preferences ->
            preferences[ACCESS_TOKEN]
            }
    val refreshToken: Flow<String?> =
        dataStore.data.map {preferences ->
            preferences[REFRESH_TOKEN]
        }
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ){
        dataStore.edit {preferences ->
            preferences[ACCESS_TOKEN]=accessToken
            preferences[REFRESH_TOKEN]=refreshToken
        }
    }
    suspend fun clearTokens(){
        dataStore.edit{preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
    private companion object {
        val ACCESS_TOKEN=
            stringPreferencesKey("access_token")
        val REFRESH_TOKEN=
            stringPreferencesKey("refresh_token")
    }
}
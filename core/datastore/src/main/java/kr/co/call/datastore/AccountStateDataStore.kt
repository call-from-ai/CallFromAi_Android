package kr.co.call.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountStateDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
){
    suspend fun markWithdrawn(){
        dataStore.edit {preferences->
            preferences[FORCE_ONBOARDING]=true
        }
    }
    suspend fun shouldForceOnboarding(): Boolean{
        return dataStore.data.first()[FORCE_ONBOARDING] ?:false
    }
    suspend fun clearForceOnboarding() {
        dataStore.edit { preferences ->
            preferences.remove(FORCE_ONBOARDING)
        }
    }

    private companion object {
        val FORCE_ONBOARDING =
            booleanPreferencesKey("force_onboarding_after_withdrawal")
    }
}
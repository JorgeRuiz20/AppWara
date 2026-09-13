package com.example.wara.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.wara.core.constants.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.PREFERENCES_NAME)

class SessionDataStore(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey(AppConstants.TOKEN_KEY)
        private val KEY_USERNAME = stringPreferencesKey(AppConstants.USERNAME_KEY)
        private val KEY_USER_ID = intPreferencesKey(AppConstants.USER_ID_KEY)
        private val KEY_BASE_URL = stringPreferencesKey(AppConstants.BASE_URL_KEY)
    }

    val baseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        val saved = preferences[KEY_BASE_URL]
        if (saved.isNullOrBlank() || saved.contains("10.") || saved.contains("192.168.") || saved.contains("127.0.0.1") || saved.contains("localhost")) {
            AppConstants.DEFAULT_BASE_URL
        } else {
            saved
        }
    }

    suspend fun saveBaseUrl(url: String) {
        val sanitized = if (!url.endsWith("/")) "$url/" else url
        context.dataStore.edit { preferences ->
            preferences[KEY_BASE_URL] = sanitized
        }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME]
    }

    val userIdFlow: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    suspend fun saveSession(token: String, username: String, userId: Int? = null) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USERNAME] = username
            if (userId != null) {
                preferences[KEY_USER_ID] = userId
            }
        }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_USERNAME)
            preferences.remove(KEY_USER_ID)
        }
    }
}

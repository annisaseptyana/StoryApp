package com.bangkit.storyapp.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class AppDataStore private constructor(private val dataStore: DataStore<Preferences>) {

    private val LOGIN_TOKEN = stringPreferencesKey("login_token")

    fun getToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[LOGIN_TOKEN]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDataStore? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppDataStore {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDataStore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.iatrading.mobile.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iatrading.mobile.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val API_BASE_URL_KEY = stringPreferencesKey("api_base_url")
    }

    /**
     * Flow that emits the current API base URL.
     * Returns the custom URL if set, otherwise returns the default from BuildConfig.
     */
    val apiBaseUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[API_BASE_URL_KEY] ?: BuildConfig.API_BASE_URL
    }

    /**
     * Get the default API base URL from BuildConfig
     */
    fun getDefaultBaseUrl(): String {
        return BuildConfig.API_BASE_URL
    }

    /**
     * Save a custom API base URL
     */
    suspend fun setApiBaseUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[API_BASE_URL_KEY] = url
        }
    }

    /**
     * Reset to the default API base URL (clears the custom URL)
     */
    suspend fun resetToDefaultUrl() {
        dataStore.edit { preferences ->
            preferences.remove(API_BASE_URL_KEY)
        }
    }

    /**
     * Check if a custom URL is currently set
     */
    val isCustomUrlSet: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences.contains(API_BASE_URL_KEY)
    }
}

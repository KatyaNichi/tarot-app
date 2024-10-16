package com.superapp.luneartarot.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATION_ENABLED] ?: true }

    val isMusicEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[MUSIC_ENABLED] ?: true }

    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[VIBRATION_ENABLED] ?: true }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[MUSIC_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[VIBRATION_ENABLED] = enabled }
    }
}
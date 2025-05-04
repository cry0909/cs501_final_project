// File: com/example/wellipet/data/AuthPreferences.kt
package com.example.wellipet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth_prefs")

object AuthPreferences {
    private val REMEMBER_ME = booleanPreferencesKey("remember_me")

    /** Read whether the user has checked "Remember me" */
    val Context.rememberMeFlow
        get() = dataStore.data.map { prefs ->
            prefs[REMEMBER_ME] ?: false
        }

    /** Save the user's "Remember me" preference */
    suspend fun Context.setRememberMe(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = value
        }
    }
}

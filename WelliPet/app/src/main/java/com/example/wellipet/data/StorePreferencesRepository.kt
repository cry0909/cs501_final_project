// File: com/example/wellipet/data/StorePreferencesRepository.kt
package com.example.wellipet.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 擴展屬性，建立一個 DataStore 實例
private val Context.dataStore by preferencesDataStore(name = "store_preferences")

object StorePreferencesKeys {
    val SELECTED_PET = intPreferencesKey("selected_pet")
    val SELECTED_BACKGROUND = intPreferencesKey("selected_background")
}

class StorePreferencesRepository(private val context: Context) {
    val selectedPet: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[StorePreferencesKeys.SELECTED_PET]
    }

    val selectedBackground: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[StorePreferencesKeys.SELECTED_BACKGROUND]
    }

    suspend fun saveSelectedPet(resId: Int) {
        context.dataStore.edit { preferences ->
            preferences[StorePreferencesKeys.SELECTED_PET] = resId
        }
    }

    suspend fun saveSelectedBackground(resId: Int) {
        context.dataStore.edit { preferences ->
            preferences[StorePreferencesKeys.SELECTED_BACKGROUND] = resId
        }
    }
}

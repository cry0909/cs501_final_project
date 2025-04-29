// File: com/example/wellipet/data/StorePreferencesRepository.kt
package com.example.wellipet.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. 建立 DataStore 實例
private val Context.dataStore by preferencesDataStore(name = "store_preferences")

// 2. 定義所有 key
object StorePreferencesKeys {
    val SELECTED_PET = intPreferencesKey("selected_pet")
    val SELECTED_BACKGROUND = intPreferencesKey("selected_background")
    val PET_BADGES = stringSetPreferencesKey("pet_badges")
}

// 3. Repository 提供 Flow 讀取與 suspend 函式寫入
class StorePreferencesRepository(private val context: Context) {

    /** 已選寵物資源 ID */
    val selectedPet: Flow<Int?> = context.dataStore.data
        .map { it[StorePreferencesKeys.SELECTED_PET] }

    /** 已選背景資源 ID */
    val selectedBackground: Flow<Int?> = context.dataStore.data
        .map { it[StorePreferencesKeys.SELECTED_BACKGROUND] }

    /** 已選徽章 ID 集合 */
    val selectedBadges: Flow<Set<String>> = context.dataStore.data
        .map { it[StorePreferencesKeys.PET_BADGES] ?: emptySet() }

    /** 儲存寵物 */
    suspend fun saveSelectedPet(resId: Int) {
        context.dataStore.edit { prefs ->
            prefs[StorePreferencesKeys.SELECTED_PET] = resId
        }
    }

    /** 儲存背景 */
    suspend fun saveSelectedBackground(resId: Int) {
        context.dataStore.edit { prefs ->
            prefs[StorePreferencesKeys.SELECTED_BACKGROUND] = resId
        }
    }

    /** 儲存徽章集合（會覆蓋之前所有選擇） */
    suspend fun saveSelectedBadges(badgeIds: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[StorePreferencesKeys.PET_BADGES] = badgeIds
        }
    }
}

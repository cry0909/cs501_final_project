// File: com/example/wellipet/data/DataStoreExt.kt
package com.example.wellipet.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// For Singleton use
val Context.dataStore by preferencesDataStore(name = "wellipet_prefs")

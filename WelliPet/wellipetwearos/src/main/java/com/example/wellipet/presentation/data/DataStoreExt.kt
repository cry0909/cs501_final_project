// File: com/example/wellipet/data/DataStoreExt.kt
package com.example.wellipet.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// 只在这儿声明一次，其他地方都 import 这个
val Context.dataStore by preferencesDataStore(name = "wellipet_prefs")

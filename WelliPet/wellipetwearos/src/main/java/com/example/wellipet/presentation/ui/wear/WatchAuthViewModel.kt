// File: com/example/wellipet/presentation/ui/wear/WatchAuthViewModel.kt
package com.example.wellipet.presentation.ui.wear

import android.app.Application
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.wellipet.data.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

private val UID_KEY = stringPreferencesKey("user_uid")

class WatchAuthViewModel(app: Application) : AndroidViewModel(app) {
    // uidFlow: null 表示還沒收到／存過 UID；非 null 才跳進 home
    val uidFlow: StateFlow<String?> = app.dataStore.data
        .map { prefs -> prefs[UID_KEY] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

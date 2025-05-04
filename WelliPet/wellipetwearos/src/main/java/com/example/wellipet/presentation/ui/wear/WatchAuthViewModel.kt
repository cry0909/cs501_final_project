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
    // uidFlow: null means the UID has not yet been received or stored;
    // only when non-null do we navigate into the home screen
    val uidFlow: StateFlow<String?> = app.dataStore.data
        .map { prefs -> prefs[UID_KEY] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

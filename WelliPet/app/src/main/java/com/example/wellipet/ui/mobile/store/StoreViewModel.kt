// File: com/example/wellipet/ui/mobile/store/StoreViewModel.kt
package com.example.wellipet.ui.mobile.store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.data.StorePreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StorePreferencesRepository(application)

    // 使用 DataStore 提供的 Flow 並轉換成 StateFlow，預設值為 null
    val selectedPet = repository.selectedPet.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val selectedBackground = repository.selectedBackground.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun selectPet(resId: Int) {
        viewModelScope.launch {
            repository.saveSelectedPet(resId)
        }
    }

    fun selectBackground(resId: Int) {
        viewModelScope.launch {
            repository.saveSelectedBackground(resId)
        }
    }
}

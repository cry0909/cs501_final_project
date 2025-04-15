// File: com/example/wellipet/ui/store/StoreViewModel.kt
package com.example.wellipet.ui.store

//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



class StoreViewModel : ViewModel() {
    private val _selectedPet = MutableStateFlow<Int?>(null)
    val selectedPet: StateFlow<Int?> = _selectedPet

    private val _selectedBackground = MutableStateFlow<Int?>(null)
    val selectedBackground: StateFlow<Int?> = _selectedBackground

    fun selectPet(resId: Int) {
        _selectedPet.value = resId
    }

    fun selectBackground(resId: Int) {
        _selectedBackground.value = resId
    }
}

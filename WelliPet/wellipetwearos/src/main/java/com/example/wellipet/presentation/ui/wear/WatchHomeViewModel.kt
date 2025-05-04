package com.example.wellipet.presentation.ui.wear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.presentation.data.repository.WatchFirebaseUserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class WatchHomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = WatchFirebaseUserRepository(app)

    val petStatus = repo.petStatusFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "happy")

    val selectedPet = repo.selectedPetFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val selectedBackground = repo.selectedBackgroundFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val selectedBadges = repo.selectedBadgesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
}

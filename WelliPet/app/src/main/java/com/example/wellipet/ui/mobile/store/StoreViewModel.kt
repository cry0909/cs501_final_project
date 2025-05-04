// File: com/example/wellipet/ui/mobile/store/StoreViewModel.kt
package com.example.wellipet.ui.mobile.store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.data.repository.FirebaseUserRepository
import com.example.wellipet.data.BadgeCalculator
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo   = FirebaseUserRepository()
    private val healthRepo = HealthRepository(application)
    private val badgeCalc  = BadgeCalculator(healthRepo)

    // Flow of badges that have been unlocked and persisted in Firestore
    private val persistedUnlockedFlow = userRepo.unlockedBadgesFlow()


    /** Flow of the currently selected pet key */
    val selectedPet: StateFlow<String?> =
        userRepo.selectedPetFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Flow of the currently selected background key */
    val selectedBackground: StateFlow<String?> =
        userRepo.selectedBackgroundFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Flow of the selected badge IDs (up to 3) from Firestore */
    val selectedBadges: StateFlow<Set<String>> =
        userRepo.selectedBadgesFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // Backing state for the set of badges that are actually unlocked
    private val _unlockedBadges = MutableStateFlow<Set<String>>(emptySet())
    val unlockedBadges: StateFlow<Set<String>> = _unlockedBadges

    init {
        // Calculate unlocked badges once on startup, then keep in sync with Firestore
        viewModelScope.launch {
            // 1) Load persisted unlocked badges
            val persisted = persistedUnlockedFlow.first()

            // 2) Compute which badges should be unlocked now
            val current = badgeCalc.calculateUnlocked()

            // 3) Merge old and new to keep historical unlocks
            val merged = persisted union current

            // 4) If there are new badges, save them back to Firestore
            if (merged != persisted) {
                userRepo.saveUnlockedBadges(merged)
            }

            // 5) Update local UI state
            _unlockedBadges.value = merged

            // 6) Continue listening for remote updates and keep UI in sync
            persistedUnlockedFlow
                .drop(1)
                .collect { updated ->
                    _unlockedBadges.value = updated
                }
        }
    }
    /** Select a pet by its key and save to Firestore */
    fun selectPet(name: String) = viewModelScope.launch {
        userRepo.saveSelectedPet(name)
    }

    /** Select a background by its key and save to Firestore */
    fun selectBackground(name: String) = viewModelScope.launch {
        userRepo.saveSelectedBackground(name)
    }

    /**
     * Toggle badge selection:
     * - If already selected, remove it
     * - If not selected and fewer than 3 badges, add it
     */
    fun toggleBadge(badgeId: String) = viewModelScope.launch {
        val current = selectedBadges.value.toMutableSet()
        if (current.contains(badgeId)) {
            current.remove(badgeId)
        } else if (current.size < 3) {
            current.add(badgeId)
        }
        userRepo.saveSelectedBadges(current)
    }
}

// File: com/example/wellipet/ui/mobile/store/StoreViewModel.kt
package com.example.wellipet.ui.mobile.store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.data.repository.FirebaseBadgeRepository
import com.example.wellipet.data.StorePreferencesRepository
import com.example.wellipet.data.BadgeCalculator
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StorePreferencesRepository(application)
    private val healthRepo  = HealthRepository(application)
    private val badgeCalc   = BadgeCalculator(healthRepo)
    private val badgeRepo = FirebaseBadgeRepository()


    /** 已選寵物資源 ID */
    val selectedPet: StateFlow<Int?> =
        repository.selectedPet
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** 已選背景資源 ID */
    val selectedBackground: StateFlow<Int?> =
        repository.selectedBackground
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** 從 Firestore 讀取已選徽章 (最多 3 個) **/
    val selectedBadges: StateFlow<Set<String>> =
        badgeRepo.selectedBadgesFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // 計算出來的「已解鎖徽章」
    private val _unlockedBadges = MutableStateFlow<Set<String>>(emptySet())
    val unlockedBadges: StateFlow<Set<String>> = _unlockedBadges

    init {
        // 啟動時計算一次
        viewModelScope.launch {
            val unlocked = badgeCalc.calculateUnlocked()
            _unlockedBadges.value = unlocked
        }
    }
    /** 選擇寵物 */
    fun selectPet(resId: Int) {
        viewModelScope.launch {
            repository.saveSelectedPet(resId)
        }
    }

    /** 選擇背景 */
    fun selectBackground(resId: Int) {
        viewModelScope.launch {
            repository.saveSelectedBackground(resId)
        }
    }

    /**
     * 切換徽章選擇狀態：
     * - 如果已經選擇，則移除
     * - 如果還沒選擇，且總數 < 3，則新增
     */
    fun toggleBadge(badgeId: String) = viewModelScope.launch {
        val current = selectedBadges.value.toMutableSet()
        if (current.contains(badgeId)) {
            current.remove(badgeId)
        } else if (current.size < 3) {
            current.add(badgeId)
        }
        badgeRepo.saveSelectedBadges(current)
    }
}

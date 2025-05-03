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

    // Firestore 中持久化的解锁列表
    private val persistedUnlockedFlow = userRepo.unlockedBadgesFlow()


    /** 已選寵物資源 ID */
    val selectedPet: StateFlow<String?> =
        userRepo.selectedPetFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** 已選背景資源 ID */
    val selectedBackground: StateFlow<String?> =
        userRepo.selectedBackgroundFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** 從 Firestore 讀取已選徽章 (最多 3 個) **/
    val selectedBadges: StateFlow<Set<String>> =
        userRepo.selectedBadgesFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // 計算出來的「已解鎖徽章」
    private val _unlockedBadges = MutableStateFlow<Set<String>>(emptySet())
    val unlockedBadges: StateFlow<Set<String>> = _unlockedBadges

    init {
        // 啟動時計算一次
        viewModelScope.launch {
            // 先取后端已保存的
            val persisted = persistedUnlockedFlow.first()

            // 再算一遍当前符合条件的
            val current = badgeCalc.calculateUnlocked()

            // 并集：保留以往 + 新达标的
            val merged = persisted union current

            // 如果有新增，就写回 Firestore
            if (merged != persisted) {
                userRepo.saveUnlockedBadges(merged)
            }

            // 更新 UI
            _unlockedBadges.value = merged

            // 继续监听后端改动，保持同步
            persistedUnlockedFlow
                .drop(1) // 跳过已经处理过的那一次
                .collect { updated ->
                    _unlockedBadges.value = updated
                }
        }
    }
    /** 選擇寵物 */
    fun selectPet(name: String) = viewModelScope.launch {
        userRepo.saveSelectedPet(name)
    }

    /** 選擇背景 */
    fun selectBackground(name: String) = viewModelScope.launch {
        userRepo.saveSelectedBackground(name)
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
        userRepo.saveSelectedBadges(current)
    }
}

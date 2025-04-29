// File: com/example/wellipet/data/BadgeCalculator.kt
package com.example.wellipet.data

import com.example.wellipet.data.repository.HealthRepository
import com.example.wellipet.ui.model.allBadgeDefinitions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async

class BadgeCalculator(private val repo: HealthRepository) {
    /** 同步計算所有已解鎖 Badge 的 id */
    suspend fun calculateUnlocked(): Set<String> = coroutineScope {
        allBadgeDefinitions.map { def ->
            async {
                if (def.unlock(repo)) def.id else null
            }
        }.mapNotNull { it.await() }.toSet()
    }
}

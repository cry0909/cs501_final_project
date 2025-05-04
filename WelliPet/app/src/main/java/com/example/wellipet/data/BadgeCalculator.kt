// File: com/example/wellipet/data/BadgeCalculator.kt
package com.example.wellipet.data

import com.example.wellipet.data.repository.HealthRepository
import com.example.wellipet.ui.model.allBadgeDefinitions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async

class BadgeCalculator(private val repo: HealthRepository) {
    // Calculate all unlocked badge IDs
    suspend fun calculateUnlocked(): Set<String> = coroutineScope {
        allBadgeDefinitions.map { def ->
            async {
                if (def.unlock(repo)) def.id else null
            }
        }.mapNotNull { it.await() }.toSet()
    }
}

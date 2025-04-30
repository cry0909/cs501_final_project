// File: com/example/wellipet/ui/model/BadgeDefinition.kt
package com.example.wellipet.ui.model

import com.example.wellipet.R
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

enum class BadgeCategory { Hydration, Steps, Sleep, Combined }

data class BadgeDefinition(
    val id: String,
    val category: BadgeCategory,
    val unlock: suspend (HealthRepository) -> Boolean,
    val unlockedIconRes: Int,
    val lockedIconRes: Int
)

val allBadgeDefinitions = listOf(
    // Hydration 系列
    BadgeDefinition(
        id = "hydration_novice",
        category = BadgeCategory.Hydration,
        unlock = { repo -> repo.readHydration() >= 2_000L },
        unlockedIconRes = R.drawable.hydration_novice,
        lockedIconRes   = R.drawable.hydration_novice_locked
    ),
    BadgeDefinition(
        id = "hydration_expert",
        category = BadgeCategory.Hydration,
        unlock = { repo ->
            // 連續 7 天每日 ≥ 2000 ml
            val hist = repo.getHistoricalHydration(7)
            hist.size >= 7 && hist.all { (_, v) -> v >= 2_000L }
        },
        unlockedIconRes = R.drawable.hydration_expert,
        lockedIconRes   = R.drawable.hydration_expert_locked
    ),
    BadgeDefinition(
        id = "hydration_master",
        category = BadgeCategory.Hydration,
        unlock = { repo ->
            val hist = repo.getHistoricalHydration(14)
            hist.size >= 14 && hist.all { (_, v) -> v >= 2_000L }
        },
        unlockedIconRes = R.drawable.hydration_master,
        lockedIconRes   = R.drawable.hydration_master_locked
    ),
    BadgeDefinition(
        id = "hydration_legend",
        category = BadgeCategory.Hydration,
        unlock = { repo ->
            val hist = repo.getHistoricalHydration(30)
            hist.size >= 30 && hist.all { (_, v) -> v >= 2_000L }
        },
        unlockedIconRes = R.drawable.hydration_legend,
        lockedIconRes   = R.drawable.hydration_legend_locked
    ),

    // Steps 系列
    BadgeDefinition(
        id = "step_beginner",
        category = BadgeCategory.Steps,
        unlock = { repo ->
            val today = repo.getHistoricalSteps(1)
            today.isNotEmpty() && today.last().second >= 5_000L
        },
        unlockedIconRes = R.drawable.step_beginner,
        lockedIconRes   = R.drawable.step_beginner_locked
    ),
    BadgeDefinition(
        id = "jogger",
        category = BadgeCategory.Steps,
        unlock = { repo ->
            val today = repo.getHistoricalSteps(1)
            today.isNotEmpty() && today.last().second >= 10_000L
        },
        unlockedIconRes = R.drawable.jogger,
        lockedIconRes   = R.drawable.jogger_locked
    ),
    BadgeDefinition(
        id = "step_sprinter",
        category = BadgeCategory.Steps,
        unlock = { repo ->
            val hist = repo.getHistoricalSteps(7)
            hist.size >= 7 && hist.all { (_, v) -> v >= 10_000L }
        },
        unlockedIconRes = R.drawable.step_sprinter,
        lockedIconRes   = R.drawable.step_sprinter_locked
    ),
    BadgeDefinition(
        id = "step_champion",
        category = BadgeCategory.Steps,
        unlock = { repo ->
            val hist = repo.getHistoricalSteps(14)
            hist.size >= 14 && hist.all { (_, v) -> v >= 10_000L }
        },
        unlockedIconRes = R.drawable.step_champion,
        lockedIconRes   = R.drawable.step_champion_locked
    ),
    BadgeDefinition(
        id = "step_legend",
        category = BadgeCategory.Steps,
        unlock = { repo ->
            val hist = repo.getHistoricalSteps(30)
            hist.size >= 30 && hist.all { (_, v) -> v >= 10_000L }
        },
        unlockedIconRes = R.drawable.step_legend,
        lockedIconRes   = R.drawable.step_legend_locked
    ),

    // Sleep 系列
    BadgeDefinition(
        id = "sleep_enthusiast",
        category = BadgeCategory.Sleep,
        unlock = { repo -> repo.readSleep() >= 7 * 3600L },
        unlockedIconRes = R.drawable.sleep_enthusiast,
        lockedIconRes   = R.drawable.sleep_enthusiast_locked
    ),
    BadgeDefinition(
        id = "dream_weaver",
        category = BadgeCategory.Sleep,
        unlock = { repo ->
            val hist = repo.getHistoricalSleep(7)
            hist.size >= 7 && hist.all { (_, v) -> v >= 7 * 3600L }
        },
        unlockedIconRes = R.drawable.dream_weaver,
        lockedIconRes   = R.drawable.dream_weaver_locked
    ),
    BadgeDefinition(
        id = "sleep_master",
        category = BadgeCategory.Sleep,
        unlock = { repo ->
            val hist = repo.getHistoricalSleep(14)
            hist.size >= 14 && hist.all { (_, v) -> v >= 7 * 3600L }
        },
        unlockedIconRes = R.drawable.sleep_master,
        lockedIconRes   = R.drawable.sleep_master_locked
    ),
    BadgeDefinition(
        id = "sleep_legend",
        category = BadgeCategory.Sleep,
        unlock = { repo ->
            val hist = repo.getHistoricalSleep(30)
            hist.size >= 30 && hist.all { (_, v) -> v >= 7 * 3600L }
        },
        unlockedIconRes = R.drawable.sleep_legend,
        lockedIconRes   = R.drawable.sleep_legend_locked
    ),

    // Combined 三項全能
    BadgeDefinition(
        id = "daily_triathlete",
        category = BadgeCategory.Combined,
        unlock = { repo ->
            // 當天三項同時達標
            val h = repo.readHydration() >= 2_000L
            val s = repo.getHistoricalSteps(1).lastOrNull()?.second ?: 0L >= 10_000L
            val z = repo.readSleep() >= 7 * 3600L
            h && s && z
        },
        unlockedIconRes = R.drawable.daily_triathlete,
        lockedIconRes   = R.drawable.daily_triathlete_locked
    ),
    BadgeDefinition(
        id = "weekly_triathlete",
        category = BadgeCategory.Combined,
        unlock = { repo ->
            val hyd = repo.getHistoricalHydration(7)
            val stp = repo.getHistoricalSteps(7)
            val slp = repo.getHistoricalSleep(7)
            hyd.size >= 7 && stp.size >= 7 && slp.size >= 7 &&
                    hyd.all { (_, v) -> v >= 2_000L } &&
                    stp.all { (_, v) -> v >= 10_000L } &&
                    slp.all { (_, v) -> v >= 7 * 3600L }
        },
        unlockedIconRes = R.drawable.weekly_triathlete,
        lockedIconRes   = R.drawable.weekly_triathlete_locked
    ),
    BadgeDefinition(
        id = "ultimate_triathlete",
        category = BadgeCategory.Combined,
        unlock = { repo ->
            val hyd = repo.getHistoricalHydration(30)
            val stp = repo.getHistoricalSteps(30)
            val slp = repo.getHistoricalSleep(30)
            hyd.size >= 30 && stp.size >= 30 && slp.size >= 30 &&
                    hyd.all { (_, v) -> v >= 2_000L } &&
                    stp.all { (_, v) -> v >= 10_000L } &&
                    slp.all { (_, v) -> v >= 7 * 3600L }
        },
        unlockedIconRes = R.drawable.ultimate_triathlete,
        lockedIconRes   = R.drawable.ultimate_triathlete_locked
    )
)

// File: com/example/wellipet/data/repository/HealthRepository.kt
package com.example.wellipet.data.repository

import android.content.Context

import com.example.wellipet.data.source.HealthConnectSource

class HealthRepository(context: Context) {
    private val healthConnectSource = HealthConnectSource(context)

    private var histSteps30: List<Pair<String,Long>>? = null
    private var histHyd30:  List<Pair<String,Long>>? = null
    private var histSleep30: List<Pair<String,Long>>? = null

    fun clearHistoryCache() { //AI reference, original issue: API quota exceeded
        // 1. Clear the repository's 30-day slice cache
        histSteps30 = null
        histHyd30   = null
        histSleep30 = null

        // 2. Also clear the cache inside HealthConnectSource
        healthConnectSource.clearHistoricalCache()
    }

    suspend fun getSteps(): Long {
        return healthConnectSource.readSteps()
    }

    suspend fun readSleep(): Long {
        return healthConnectSource.readSleep()
    }

    suspend fun readHydration(): Long {
        return healthConnectSource.readHydration()
    }

    suspend fun addHydration(hydrationML: Long): Boolean {
        return healthConnectSource.addHydration(hydrationML)
    }
    // for pet_status check
    suspend fun getHydrationLast(hours: Long) = healthConnectSource.readHydration(hours)
    suspend fun getStepsLast(hours: Long)     = healthConnectSource.readSteps(hours)


    // Historical data functions
    suspend fun getHistoricalSteps(days: Int): List<Pair<String,Long>> {
        if (histSteps30 == null)
            histSteps30 = healthConnectSource.readHistoricalSteps(30)
        return histSteps30!!.takeLast(days)
    }
    suspend fun getHistoricalHydration(days: Int): List<Pair<String,Long>> {
        if (histHyd30 == null)
            histHyd30 = healthConnectSource.readHistoricalHydration(30)
        return histHyd30!!.takeLast(days)
    }
    suspend fun getHistoricalSleep(days: Int): List<Pair<String,Long>> {
        if (histSleep30 == null)
            histSleep30 = healthConnectSource.readHistoricalSleep(30)
        return histSleep30!!.takeLast(days)
    }

}

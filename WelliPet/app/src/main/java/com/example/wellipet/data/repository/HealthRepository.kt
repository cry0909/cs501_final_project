// File: com/example/wellipet/data/repository/HealthRepository.kt
package com.example.wellipet.data.repository

import android.content.Context
import com.example.wellipet.data.source.HealthConnectSource

class HealthRepository(context: Context) {
    private val healthConnectSource = HealthConnectSource(context)

    suspend fun addSteps(steps: Long): Boolean {
        return healthConnectSource.insertSteps(steps)
    }

    suspend fun getSteps(): Long {
        return healthConnectSource.readSteps()
    }
}

// File: com/example/wellipet/data/repository/HealthRepository.kt
package com.example.wellipet.data.repository

import android.content.Context
import androidx.room.Room
import com.example.wellipet.data.model.AppDatabase
import com.example.wellipet.data.model.StepCount
import com.example.wellipet.data.source.StepCounterSensor
import com.example.wellipet.data.source.HealthConnectSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class HealthRepository(context: Context) {
    private val healthConnectSource = HealthConnectSource(context)
    private val stepCounterSensor = StepCounterSensor(context)

    // Room Database
//    private val db = Room.databaseBuilder(
//        context,
//        AppDatabase::class.java,
//        "wellipet-db"
//    ).build()
//    private val stepsDao = db.stepsDao()

    suspend fun addSteps(steps: Long): Boolean {
        return healthConnectSource.insertSteps(steps)
    }

    suspend fun getSteps(): Long {
        return healthConnectSource.readSteps()
    }
    // 使用感應器取得步數的介面
    suspend fun getSensorSteps(): Long {
        return stepCounterSensor.getSteps()
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

    // 歷史資料函式
    suspend fun getHistoricalSteps(days: Int = 30): List<Pair<String, Long>> {
        return healthConnectSource.readHistoricalSteps(days)
    }

    suspend fun getHistoricalSleep(days: Int = 7): List<Pair<String, Long>> {
        return healthConnectSource.readHistoricalSleep(days)
    }

    suspend fun getHistoricalHydration(days: Int = 7): List<Pair<String, Long>> {
        return healthConnectSource.readHistoricalHydration(days)
    }

    // 存入感應器讀取到的步數歷史記錄
//    suspend fun storeSteps(steps: Long) = withContext(Dispatchers.IO) {
//        val stepCount = StepCount(
//            steps = steps,
//            createdAt = Instant.now().toString()
//        )
//        stepsDao.insertAll(stepCount)
//    }

    // 載入全部歷史資料 (或根據需求變更查詢條件)
//    suspend fun loadHistoricalSteps(): List<StepCount> = withContext(Dispatchers.IO) {
//        stepsDao.getAll()
//    }
}

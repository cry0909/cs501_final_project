// File: com/example/wellipet/data/source/HealthConnectSource.kt
package com.example.wellipet.data.source

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.LocalDate
import java.time.ZoneId

class HealthConnectSource(context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    suspend fun insertSteps(steps: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofMinutes(5))
            val stepsRecord = StepsRecord(
                count = steps,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.UTC,
                endZoneOffset = ZoneOffset.UTC,
                metadata = Metadata.autoRecorded(
                    device = Device(type = Device.TYPE_WATCH)
                )
            )
            healthConnectClient.insertRecords(listOf(stepsRecord))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun readSteps(): Long = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofHours(24))
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, now)
            )
            val response = healthConnectClient.readRecords(request)
            response.records.sumOf { it.count }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    suspend fun readSleep(): Long = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofHours(24))
            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, now)
            )
            val response = healthConnectClient.readRecords(request)
            // 累加所有睡眠記錄的時長（秒）
            var totalSleepSeconds = 0L
            response.records.forEach { sleepRecord ->
                totalSleepSeconds += Duration.between(sleepRecord.startTime, sleepRecord.endTime).seconds
            }
            totalSleepSeconds
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }


    // 讀取飲水資料：過去 24 小時內所有 HydrationRecord 的水量總和（假設以毫升計算）
    suspend fun readHydration(): Long = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofHours(24))
            val request = ReadRecordsRequest(
                recordType = HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, now)
            )
            val response = healthConnectClient.readRecords(request)
            var totalHydrationMl = 0L
            response.records.forEach { hydrationRecord ->
                totalHydrationMl += hydrationRecord.volume.inMilliliters.toLong()
            }
            totalHydrationMl
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    suspend fun addHydration(hydrationML: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofMinutes(1))
            // 建立 HydrationRecord,
            val hydrationRecord = HydrationRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.UTC,
                endTime = now,
                endZoneOffset = ZoneOffset.UTC,
                volume = androidx.health.connect.client.units.Volume.milliliters(hydrationML.toDouble()),
                metadata = Metadata.autoRecorded(
                    device = Device(type = Device.TYPE_PHONE)
                )
            )
            healthConnectClient.insertRecords(listOf(hydrationRecord))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // 讀取歷史步數：取得過去 N 天每天的總步數
    suspend fun readHistoricalSteps(days: Int = 30): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val results = mutableListOf<Pair<String, Long>>()

        for (i in 0 until days) {
            val date = today.minusDays(i.toLong())
            // 這一天的開始：當地時區的 yyyy-MM-ddT00:00
            val startInstant = date.atStartOfDay(zone).toInstant()
            // 這一天的結束（不含）：next day 00:00
            val endInstant = date.plusDays(1).atStartOfDay(zone).toInstant()

            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
            )
            val response = healthConnectClient.readRecords(request)
            val totalSteps = response.records.sumOf { it.count }
            results.add(Pair(date.toString(), totalSteps))
        }
        results.reverse() // 最早的在前
        results
    }

    // 讀取歷史睡眠：取得過去 N 天每天的總睡眠秒數
    suspend fun readHistoricalSleep(days: Int = 7): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val results = mutableListOf<Pair<String, Long>>()

        for (i in 0 until days) {
            val date = today.minusDays(i.toLong())
            val start = date.atStartOfDay(zone).toInstant()
            val end   = date.plusDays(1).atStartOfDay(zone).toInstant()

            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            val totalSeconds = response.records.sumOf { record ->
                Duration.between(record.startTime, record.endTime).seconds
            }
            results.add(Pair(date.toString(), totalSeconds))
        }
        results.reverse()
        results
    }

    // 讀取歷史飲水：取得過去 N 天每天的總飲水量（毫升）
    suspend fun readHistoricalHydration(days: Int = 7): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val results = mutableListOf<Pair<String, Long>>()

        for (i in 0 until days) {
            val date = today.minusDays(i.toLong())
            val start = date.atStartOfDay(zone).toInstant()
            val end   = date.plusDays(1).atStartOfDay(zone).toInstant()

            val request = ReadRecordsRequest(
                recordType = HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            val total = response.records.sumOf { it.volume.inMilliliters.toLong() }
            results.add(Pair(date.toString(), total))
        }
        results.reverse()
        results
    }
}

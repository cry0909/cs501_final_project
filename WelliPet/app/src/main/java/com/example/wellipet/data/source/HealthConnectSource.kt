// File: com/example/wellipet/data/source/HealthConnectSource.kt
package com.example.wellipet.data.source

import android.content.Context
import android.health.connect.HealthConnectException
import android.util.Log
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
import java.time.format.DateTimeFormatter

class HealthConnectSource(context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)
    // 缓存上次读到的值
    private var lastStepsCache: Long = 0L
    private var lastSleepCache: Long = 0L
    private var lastHydrationCache: Long = 0L

    private val lastHistoricalStepsCache = mutableMapOf<Int, List<Pair<String, Long>>>()
    private val lastHistoricalSleepCache = mutableMapOf<Int, List<Pair<String, Long>>>()
    private val lastHistoricalHydrationCache = mutableMapOf<Int, List<Pair<String, Long>>>()

    /** 清掉历史查询的内部缓存，force next call 真正去 Health Connect API */
    fun clearHistoricalCache() {
        lastHistoricalStepsCache.clear()
        lastHistoricalSleepCache.clear()
        lastHistoricalHydrationCache.clear()
    }

    suspend fun readSteps(hours: Long = 24): Long = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofHours(hours))
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, now)
            )
            val response = healthConnectClient.readRecords(request)
            val total = response.records.sumOf { it.count }
            lastStepsCache = total
            total
        } catch (e: HealthConnectException) {
            // 配额超限或其他 Health Connect 错误
            Log.w("HealthConnect", "readSteps failed: ${e.errorCode}", e)
            lastStepsCache
        } catch (e: Exception) {
            e.printStackTrace()
            lastStepsCache
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
                totalSleepSeconds += Duration.between(
                    sleepRecord.startTime,
                    sleepRecord.endTime
                ).seconds.coerceAtLeast(0L)
            }
            // 成功拿到新的值，就更新快取
            lastSleepCache = totalSleepSeconds
            totalSleepSeconds
        } catch (e: HealthConnectException) {
            // quota 超限或 Health Connect 特有錯誤
            e.printStackTrace()
            lastSleepCache
        } catch (e: Exception) {
            // 其它任何 Exception
            e.printStackTrace()
            lastSleepCache
        }
    }



    // 讀取飲水資料：過去 24 小時內所有 HydrationRecord 的水量總和（假設以毫升計算）
    suspend fun readHydration(hours: Long = 24): Long = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val startTime = now.minus(Duration.ofHours(hours))
            val req = ReadRecordsRequest(
                recordType = HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, now)
            )
            val resp = healthConnectClient.readRecords(req)
            val total = resp.records.sumOf { it.volume.inMilliliters.toLong() }
            lastHydrationCache = total
            total
        } catch (e: HealthConnectException) {
            Log.w("HealthConnect", "readHydration failed: ${e.errorCode}", e)
            lastHydrationCache
        } catch (e: Exception) {
            e.printStackTrace()
            lastHydrationCache
        }
    }
    suspend fun addHydration(hydrationML: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now()
            val rec = HydrationRecord(
                startTime = now.minus(Duration.ofMinutes(1)),
                startZoneOffset = ZoneOffset.UTC,
                endTime = now,
                endZoneOffset = ZoneOffset.UTC,
                volume = androidx.health.connect.client.units.Volume.milliliters(hydrationML.toDouble()),
                metadata = Metadata.autoRecorded(Device(type = Device.TYPE_PHONE))
            )
            healthConnectClient.insertRecords(listOf(rec))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * 一次拉過去 [days] 天的所有 StepsRecord，然後按 LocalDate group & sum，回傳
     */
    suspend fun readHistoricalSteps(days: Int): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        // 如果已經快取過，就直接回
        lastHistoricalStepsCache[days]?.let { return@withContext it }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        // 從 days 天前的 00:00 拉到現在
        val startInstant = today.minusDays(days.toLong()).atStartOfDay(zone).toInstant()
        val endInstant = Instant.now()

        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType     = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
                )
            )
            // group by 當天
            val grouped: Map<LocalDate, List<StepsRecord>> = response.records.groupBy {
                it.startTime.atZone(zone).toLocalDate()
            }
            // 建立結果列表，空的補 0
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val list = (0 until days).map { i ->
                val date = today.minusDays(i.toLong())
                val total = grouped[date]?.sumOf { it.count } ?: 0L
                formatter.format(date) to total
            }.reversed()

            lastHistoricalStepsCache[days] = list
            list
        } catch (e: HealthConnectException) {
            Log.w("HealthConnect", "readHistoricalSteps failed: ${e.errorCode}", e)
            lastHistoricalStepsCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalStepsCache[days] ?: emptyList()
        }
    }

    /**
     * 一次拉過去 [days] 天的所有 SleepSessionRecord，group by 日期後 sum duration
     */
    suspend fun readHistoricalSleep(days: Int): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        lastHistoricalSleepCache[days]?.let { return@withContext it }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startInstant = today.minusDays(days.toLong()).atStartOfDay(zone).toInstant()
        val endInstant = Instant.now()

        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType     = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
                )
            )
            val grouped = response.records.groupBy {
                it.startTime.atZone(zone).toLocalDate()
            }
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val list = (0 until days).map { i ->
                val date = today.minusDays(i.toLong())
                val totalSeconds = grouped[date]
                    ?.sumOf { r ->
                        Duration.between(r.startTime, r.endTime)
                            .seconds.coerceAtLeast(0L)
                    } ?: 0L
                formatter.format(date) to totalSeconds
            }.reversed()

            lastHistoricalSleepCache[days] = list
            list
        } catch (e: HealthConnectException) {
            Log.w("HealthConnect", "readHistoricalSleep failed: ${e.errorCode}", e)
            lastHistoricalSleepCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalSleepCache[days] ?: emptyList()
        }
    }


    /**
     * 一次拉過去 [days] 天的所有 HydrationRecord，group by 日期後 sum volume
     */
    suspend fun readHistoricalHydration(days: Int): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        lastHistoricalHydrationCache[days]?.let { return@withContext it }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startInstant = today.minusDays(days.toLong()).atStartOfDay(zone).toInstant()
        val endInstant = Instant.now()

        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType     = HydrationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
                )
            )
            val grouped = response.records.groupBy {
                it.startTime.atZone(zone).toLocalDate()
            }
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val list = (0 until days).map { i ->
                val date = today.minusDays(i.toLong())
                val totalMl = grouped[date]
                    ?.sumOf { it.volume.inMilliliters.toLong() }
                    ?: 0L
                formatter.format(date) to totalMl
            }.reversed()

            lastHistoricalHydrationCache[days] = list
            list
        } catch (e: HealthConnectException) {
            Log.w("HealthConnect", "readHistoricalHydration failed: ${e.errorCode}", e)
            lastHistoricalHydrationCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalHydrationCache[days] ?: emptyList()
        }
    }
}

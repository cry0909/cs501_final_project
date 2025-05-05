// File: com/example/wellipet/data/source/HealthConnectSource.kt
package com.example.wellipet.data.source

import android.content.Context
import android.health.connect.HealthConnectException
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
    // Cache the last retrieved values
    private var lastStepsCache: Long = 0L
    private var lastSleepCache: Long = 0L
    private var lastHydrationCache: Long = 0L

    private val lastHistoricalStepsCache = mutableMapOf<Int, List<Pair<String, Long>>>()
    private val lastHistoricalSleepCache = mutableMapOf<Int, List<Pair<String, Long>>>()
    private val lastHistoricalHydrationCache = mutableMapOf<Int, List<Pair<String, Long>>>()

    /** Clear historical internal cache so the next call will truly query Health Connect API */
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
            // Sum the duration (in seconds) of all sleep records
            var totalSleepSeconds = 0L
            response.records.forEach { sleepRecord ->
                totalSleepSeconds += Duration.between(
                    sleepRecord.startTime,
                    sleepRecord.endTime
                ).seconds.coerceAtLeast(0L)
            }
            // Update cache only if successfully retrieved new values
            lastSleepCache = totalSleepSeconds
            totalSleepSeconds
        } catch (e: HealthConnectException) {
            // quota exceeded or Health Connect-specific errors
            e.printStackTrace()
            lastSleepCache
        } catch (e: Exception) {
            // Any other exception
            e.printStackTrace()
            lastSleepCache
        }
    }



    // Read hydration data: Sum the volume of all HydrationRecords in the past 24 hours (assumed in milliliters)
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
     * Retrieve all StepsRecords from the past [days] days,
     * group and sum them by LocalDate, and return the result.
     */
    suspend fun readHistoricalSteps(days: Int): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        // If already cached, return directly
        lastHistoricalStepsCache[days]?.let { return@withContext it }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startInstant = today.minusDays(days.toLong()).atStartOfDay(zone).toInstant()
        val endInstant = Instant.now()

        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType     = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
                )
            )
            // Group by day
            val grouped: Map<LocalDate, List<StepsRecord>> = response.records.groupBy {
                it.startTime.atZone(zone).toLocalDate()
            }
            // Build result list, fill missing days with 0
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val list = (0 until days).map { i ->
                val date = today.minusDays(i.toLong())
                val total = grouped[date]?.sumOf { it.count } ?: 0L
                formatter.format(date) to total
            }.reversed()

            lastHistoricalStepsCache[days] = list
            list
        } catch (e: HealthConnectException) {
            lastHistoricalStepsCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalStepsCache[days] ?: emptyList()
        }
    }

    /**
     * Retrieve all SleepSessionRecords from the past [days] days,
     * group by date, and sum the duration.
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
            lastHistoricalSleepCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalSleepCache[days] ?: emptyList()
        }
    }


    /**
     * Retrieve all HydrationRecords from the past [days] days,
     * group by date, and sum the volume.
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
            lastHistoricalHydrationCache[days] ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            lastHistoricalHydrationCache[days] ?: emptyList()
        }
    }
}

// File: com/example/wellipet/worker/HealthCheckWorker.kt
package com.example.wellipet.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wellipet.data.repository.HealthRepository
import com.example.wellipet.data.repository.FirebaseUserRepository

class HealthCheckWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        Log.d("HealthCheckWorker", "🏃‍♂️ doWork() start at ${System.currentTimeMillis()}")

        val healthRepo = HealthRepository(applicationContext)
        val userRepo = FirebaseUserRepository()

        // 过去 1h 的喝水量
        val water1h = runCatching { healthRepo.getHydrationLast(hours = 1) }
            .getOrDefault(0L)
        // 过去 2h 的步数
        val steps2h = runCatching { healthRepo.getStepsLast(hours = 2) }
            .getOrDefault(0L)

        val status = when {
            water1h < 100L -> "thirsty"
            steps2h <  25L -> "sleepy"
            else           -> "happy"
        }

        // 写到 Firestore
        userRepo.savePetStatus(status)

        // TODO: 如果需要发系统通知，也可以在这里调用 NotificationManager
        Log.d("HealthCheckWorker", "✅ doWork() end, new status = $status")

        return Result.success()
    }
}

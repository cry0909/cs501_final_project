// File: com/example/wellipet/MainActivity.kt
package com.example.wellipet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wellipet.navigation.AppNavHost
import com.example.wellipet.ui.theme.WelliPetTheme
import com.example.wellipet.worker.HealthCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    companion object {
        const val CHANNEL_ID = "health_check_channel"
        private const val UNIQUE_WORK_NAME = "health_check"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) 先创建通知渠道（Android O+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                "Health Check Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to drink water and move around"
            }
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(chan)
        }

        // 2) 提交周期性任务：15 分钟执行一次 HealthCheckWorker
        val workRequest = PeriodicWorkRequestBuilder<HealthCheckWorker>(
            /* repeatInterval = */ 15, TimeUnit.MINUTES
        )
            .setInitialDelay(0, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("health_check")
            .observe(this) { workInfos ->
                workInfos.forEach { info ->
                    Log.d("WorkManagerDebug", "Work ${info.id} state: ${info.state}")
                }
            }

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

        setContent {
            WelliPetTheme {
                AppNavHost()
            }
        }
    }
}

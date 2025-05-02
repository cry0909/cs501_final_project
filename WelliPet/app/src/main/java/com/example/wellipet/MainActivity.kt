// File: com/example/wellipet/MainActivity.kt
package com.example.wellipet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wellipet.navigation.AppNavHost
import com.example.wellipet.ui.theme.WelliPetTheme
import com.example.wellipet.worker.HealthCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) 建立週期性工作：每 1 小時執行一次 HealthCheckWorker
        val workRequest = PeriodicWorkRequestBuilder<HealthCheckWorker>(
            /* repeatInterval = */ 15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    // 你還可以加其他約束：.setRequiresBatteryNotLow(true)、.setRequiresStorageNotLow(true)…
                    .build()
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

//         2) 提交給 WorkManager，使用唯一名稱避免重複註冊
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            /* uniqueWorkName = */ "health_check",
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

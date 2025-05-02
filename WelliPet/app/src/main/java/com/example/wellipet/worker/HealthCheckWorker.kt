// File: com/example/wellipet/worker/HealthCheckWorker.kt
package com.example.wellipet.worker

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wellipet.MainActivity
import com.example.wellipet.data.repository.HealthRepository
import com.example.wellipet.data.repository.FirebaseUserRepository
import com.google.firebase.auth.FirebaseAuth

class HealthCheckWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        Log.d("HealthCheckWorker", "🏃‍♂️ doWork() start at ${System.currentTimeMillis()}")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // 或者返回 Result.retry()，看你想让它下次再试，还是认定“跳过”就成功
            return Result.success()
        }

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
        Log.d("HealthCheckWorker", "✅ doWork() end, new status = $status")

        // 构建通知
        val notificationText = when {
            water1h < 100L && steps2h <  25L -> "You haven't drunk enough water or moved enough. Go drink some water and move around!"
            water1h < 100L                    -> "You haven’t had enough water in the past hour, remember to drink water!"
            steps2h <  25L                    -> "You are not active enough, get up and move around!"
            else                              -> "Great! Keep up the good habits!"
        }
        sendNotification("Health Status Reminder ", notificationText)


        return Result.success()
    }

    private fun sendNotification(title: String, text: String) {
        // 1) Android 13+ 需要在运行时检查 POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return
            }
        }

        // 2) 再尝试发送，catch 一下万一还抛 SecurityException
        try {
            val notif = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(System.currentTimeMillis().toInt(), notif)

        } catch (e: SecurityException) {
            Log.e("HealthCheckWorker", "发送通知失败: 没有权限", e)
        }
    }
}

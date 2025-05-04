// File: com/example/wellipet/worker/HealthCheckWorker.kt
package com.example.wellipet.worker

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // If no user is signed in, consider this work done or retry later as desired
            return Result.success()
        }

        val healthRepo = HealthRepository(applicationContext)
        val userRepo = FirebaseUserRepository()

        // Water intake in the past hour
        val water1h = runCatching { healthRepo.getHydrationLast(hours = 1) }
            .getOrDefault(0L)
        // Step count in the past two hours
        val steps2h = runCatching { healthRepo.getStepsLast(hours = 2) }
            .getOrDefault(0L)

        val status = when {
            water1h < 100L -> "thirsty"
            steps2h <  25L -> "sleepy"
            else           -> "happy"
        }

        // Update Firestore with the new pet status
        userRepo.savePetStatus(status)

        // Build appropriate notification text
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
        // 1) On Android 13+ check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return
            }
        }

        // 2) Build and post the notification, catching any security exceptions
        try {
            val notif = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(System.currentTimeMillis().toInt(), notif)

        } catch (_: SecurityException) {
        }
    }
}

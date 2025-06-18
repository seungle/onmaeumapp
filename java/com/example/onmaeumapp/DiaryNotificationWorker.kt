package com.example.onmaeumapp

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class DiaryNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        private const val TAG = "DiaryNotificationWorker"
        private const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        return try {
            val notification = createNotification()
            showNotification(notification)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
            Result.failure()
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, "diary_channel")
            .setContentTitle(context.getString(R.string.diary_notification_title))
            .setContentText(context.getString(R.string.diary_notification_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    private fun showNotification(notification: NotificationCompat.Builder) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification.build())
    }
}
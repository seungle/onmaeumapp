package com.example.onmaeumapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.onmaeumapp.MainActivity
import com.example.onmaeumapp.R

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 명상 알림 채널
            val meditationChannel = NotificationChannel(
                CHANNEL_MEDITATION,
                context.getString(R.string.channel_meditation_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_meditation_description)
            }

            // 일기 알림 채널
            val diaryChannel = NotificationChannel(
                CHANNEL_DIARY,
                context.getString(R.string.channel_diary_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_diary_description)
            }

            // 스트레스 알림 채널
            val stressChannel = NotificationChannel(
                CHANNEL_STRESS,
                context.getString(R.string.channel_stress_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_stress_description)
            }

            notificationManager.createNotificationChannels(listOf(meditationChannel, diaryChannel, stressChannel))
        }
    }

    fun showMeditationReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_MEDITATION,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MEDITATION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.meditation_reminder_title))
            .setContentText(context.getString(R.string.meditation_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_MEDITATION, notification)
    }

    fun showDiaryReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_DIARY,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DIARY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.diary_reminder_title))
            .setContentText(context.getString(R.string.diary_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_DIARY, notification)
    }

    fun showStressAlert(stressLevel: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_STRESS,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STRESS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.stress_alert_title))
            .setContentText(context.getString(R.string.stress_alert_text, stressLevel))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_STRESS, notification)
    }

    companion object {
        const val CHANNEL_MEDITATION = "meditation_channel"
        const val CHANNEL_DIARY = "diary_channel"
        const val CHANNEL_STRESS = "stress_channel"

        const val NOTIFICATION_MEDITATION = 1
        const val NOTIFICATION_DIARY = 2
        const val NOTIFICATION_STRESS = 3

        const val REQUEST_MEDITATION = 1
        const val REQUEST_DIARY = 2
        const val REQUEST_STRESS = 3
    }
} 
package com.example.onmaeumapp.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
class NotificationManager(private val context: Context) {

    fun scheduleMeditationReminder(hour: Int, minute: Int) {
        val workRequest = createPeriodicWorkRequest(
            NotificationWorker.TYPE_MEDITATION,
            hour,
            minute
        )
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_MEDITATION,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleDiaryReminder(hour: Int, minute: Int) {
        val workRequest = createPeriodicWorkRequest(
            NotificationWorker.TYPE_DIARY,
            hour,
            minute
        )
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_DIARY,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleStressCheck() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        ).setInputData(
            workDataOf(NotificationWorker.KEY_TYPE to NotificationWorker.TYPE_STRESS)
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_STRESS,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun createPeriodicWorkRequest(type: String, hour: Int, minute: Int): PeriodicWorkRequest {
        val initialDelay = calculateInitialDelay(hour, minute)

        return PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(NotificationWorker.KEY_TYPE to type))
            .build()
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - now
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(WORK_MEDITATION)
            cancelUniqueWork(WORK_DIARY)
            cancelUniqueWork(WORK_STRESS)
        }
    }

    companion object {
        private const val WORK_MEDITATION = "work_meditation"
        private const val WORK_DIARY = "work_diary"
        private const val WORK_STRESS = "work_stress"
    }
} 
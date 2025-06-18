package com.example.onmaeumapp.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.onmaeumapp.data.repository.EmotionRepository
import java.util.*
import com.example.onmaeumapp.data.AppDatabase


class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationHelper = NotificationHelper(context)
    private val emotionRepository = EmotionRepository(AppDatabase.getDatabase(context).emotionEntryDao())

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()

        when (type) {
            TYPE_MEDITATION -> {
                notificationHelper.showMeditationReminder()
            }
            TYPE_DIARY -> {
                notificationHelper.showDiaryReminder()
            }
            TYPE_STRESS -> {
                checkStressLevel()
            }
        }

        return Result.success()
    }

    private suspend fun checkStressLevel() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endDate = calendar.time

        val entries = emotionRepository.getEntriesByDateRange(startDate, endDate)
        val averageStressLevel = entries.map { it.stressLevel }.average()

        if (averageStressLevel >= 7) {
            notificationHelper.showStressAlert(averageStressLevel.toInt())
        }
    }

    companion object {
        const val KEY_TYPE = "notification_type"
        const val TYPE_MEDITATION = "meditation"
        const val TYPE_DIARY = "diary"
        const val TYPE_STRESS = "stress"
    }
} 
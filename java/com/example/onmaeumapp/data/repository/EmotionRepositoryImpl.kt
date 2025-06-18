package com.example.onmaeumapp.data.repository

import com.example.onmaeumapp.data.dao.EmotionEntryDao
import com.example.onmaeumapp.data.model.EmotionEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

class EmotionRepositoryImpl(
    private val emotionEntryDao: EmotionEntryDao
) : EmotionRepository {
    override fun getAllEntries(): Flow<List<EmotionEntry>> = emotionEntryDao.getAllEntries()

    override suspend fun getEntryById(id: Long): EmotionEntry? {
        return emotionEntryDao.getEntryByDate(Date(id))
    }

    override suspend fun insertEntry(entry: EmotionEntry) {
        emotionEntryDao.insertEntry(entry)
    }

    override suspend fun updateEntry(entry: EmotionEntry) {
        emotionEntryDao.updateEntry(entry)
    }

    override suspend fun deleteEntry(entry: EmotionEntry) {
        emotionEntryDao.deleteEntry(entry)
    }

    override suspend fun deleteEntryById(id: Long) {
        getEntryById(id)?.let { deleteEntry(it) }
    }

    override fun getEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<EmotionEntry>> {
        return emotionEntryDao.getEntriesBetweenDates(startDate, endDate)
    }

    override suspend fun getAverageStressLevel(startDate: Date, endDate: Date): Float? {
        return emotionEntryDao.getAverageStressLevel(startDate, endDate)?.toFloat()
    }

    override suspend fun getMeditationCompletionCount(startDate: Date, endDate: Date): Int {
        return emotionEntryDao.getMeditationCompletionCount(startDate, endDate)
    }

    override suspend fun getEntriesByDateRange(startDate: Date, endDate: Date): List<EmotionEntry> {
        return emotionEntryDao.getEntriesByDateRange(startDate, endDate)
    }

    override suspend fun getAllEmotions(): List<EmotionEntry> {
        return getAllEntries().first()
    }

    override suspend fun deleteAllEmotions() {
        emotionEntryDao.deleteAllEntries()
    }

    override suspend fun insertEmotions(emotions: List<EmotionEntry>) {
        emotionEntryDao.insertEntries(emotions)
    }
} 
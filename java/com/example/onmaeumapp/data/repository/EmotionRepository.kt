package com.example.onmaeumapp.data.repository

import com.example.onmaeumapp.data.model.EmotionEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface EmotionRepository {
    fun getAllEntries(): Flow<List<EmotionEntry>>
    suspend fun getEntryById(id: Long): EmotionEntry?
    suspend fun insertEntry(entry: EmotionEntry)
    suspend fun updateEntry(entry: EmotionEntry)
    suspend fun deleteEntry(entry: EmotionEntry)
    suspend fun deleteEntryById(id: Long)
    fun getEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<EmotionEntry>>
    suspend fun getAverageStressLevel(startDate: Date, endDate: Date): Float?
    suspend fun getMeditationCompletionCount(startDate: Date, endDate: Date): Int
    suspend fun getEntriesByDateRange(startDate: Date, endDate: Date): List<EmotionEntry>
    suspend fun getAllEmotions(): List<EmotionEntry>
    suspend fun deleteAllEmotions()
    suspend fun insertEmotions(emotions: List<EmotionEntry>)
} 
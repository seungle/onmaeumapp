package com.example.onmaeumapp.data.db

import androidx.room.*
import com.example.onmaeumapp.data.model.EmotionEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EmotionEntryDao {
    @Query("SELECT * FROM emotion_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<EmotionEntry>>

    @Query("SELECT * FROM emotion_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<EmotionEntry>>

    @Query("SELECT * FROM emotion_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): EmotionEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EmotionEntry): Long

    @Update
    suspend fun updateEntry(entry: EmotionEntry)

    @Delete
    suspend fun deleteEntry(entry: EmotionEntry)

    @Query("SELECT AVG(stressLevel) FROM emotion_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getAverageStressLevel(startDate: Date, endDate: Date): Float?

    @Query("SELECT COUNT(*) FROM emotion_entries WHERE meditationCompleted = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getMeditationCompletionCount(startDate: Date, endDate: Date): Int
} 
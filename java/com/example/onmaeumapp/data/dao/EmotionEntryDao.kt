package com.example.onmaeumapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.onmaeumapp.data.model.EmotionEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EmotionEntryDao {
        @Query("SELECT * FROM emotion_entries ORDER BY date DESC")
        fun getAllEntries(): Flow<List<EmotionEntry>>

        @Query("SELECT * FROM emotion_entries WHERE date = :date LIMIT 1")
        suspend fun getEntryByDate(date: Date): EmotionEntry?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertEntry(entry: EmotionEntry)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertEntries(entries: List<EmotionEntry>)

        @Query("DELETE FROM emotion_entries")
        suspend fun deleteAllEntries()

        @Update
        suspend fun updateEntry(entry: EmotionEntry)

        @Delete
        suspend fun deleteEntry(entry: EmotionEntry)

        @Query("SELECT * FROM emotion_entries WHERE date BETWEEN :starDate AND :endDate")
        fun getEntriesBetweenDates(starDate: Date, endDate: Date): Flow<List<EmotionEntry>>


        @Query("SELECT AVG(stressLevel) FROM emotion_entries WHERE date BETWEEN :start AND :end")
        suspend fun getAverageStressLevel(start: Date, end: Date): Double?

        @Query("SELECT * FROM emotion_entries WHERE date BETWEEN :startDate AND :endDate")
        suspend fun getEntriesByDateRange(startDate: Date, endDate: Date): List<EmotionEntry>

        @Query("""
    SELECT COUNT(*) FROM emotion_entries
    WHERE meditationCompleted = 1 
    AND date BETWEEN :startDate AND :endDate
""")
        suspend fun getMeditationCompletionCount(startDate: Date, endDate: Date): Int


} 
package com.example.onmaeumapp.data.dao

import androidx.room.*
import com.example.onmaeumapp.data.model.MeditationEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MeditationEntryDao {
    @Query("SELECT * FROM meditation_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<MeditationEntry>>

    @Query("SELECT * FROM meditation_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): MeditationEntry?

    @Query("SELECT * FROM meditation_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesBetweenDates(startDate: Date, endDate: Date): Flow<List<MeditationEntry>>

    @Query("SELECT * FROM meditation_entries WHERE meditationContentId = :contentId ORDER BY date DESC")
    fun getEntriesByContentId(contentId: Long): Flow<List<MeditationEntry>>

    @Query("SELECT COUNT(*) FROM meditation_entries WHERE completed = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getCompletedCount(startDate: Date, endDate: Date): Int

    @Query("SELECT AVG(duration) FROM meditation_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getAverageDuration(startDate: Date, endDate: Date): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MeditationEntry): Long

    @Update
    suspend fun updateEntry(entry: MeditationEntry)

    @Delete
    suspend fun deleteEntry(entry: MeditationEntry)

    @Query("DELETE FROM meditation_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("SELECT * FROM meditation_entries ORDER BY date DESC")
    suspend fun getAllMeditations(): List<MeditationEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditations(meditations: List<MeditationEntry>)

    @Query("DELETE FROM meditation_entries")
    suspend fun deleteAllMeditations()
}
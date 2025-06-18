package com.example.onmaeumapp.data.dao

import androidx.room.*
import com.example.onmaeumapp.data.model.MeditationContent
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationContentDao {
    @Query("SELECT * FROM meditation_contents")
    fun getAllContents(): Flow<List<MeditationContent>>

    @Query("SELECT * FROM meditation_contents WHERE id = :id")
    suspend fun getContentById(id: Long): MeditationContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: MeditationContent)

    @Update
    suspend fun updateContent(content: MeditationContent)

    @Delete
    suspend fun deleteContent(content: MeditationContent)

    @Query("SELECT * FROM meditation_contents WHERE category = :category")
    fun getContentsByCategory(category: String): Flow<List<MeditationContent>>
} 
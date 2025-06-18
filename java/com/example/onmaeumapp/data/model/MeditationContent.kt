package com.example.onmaeumapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditation_contents")
data class MeditationContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val duration: Int, // 분 단위
    val difficulty: String,
    val audioUrl: String,
    val imageUrl: String,
    val category: String
) 
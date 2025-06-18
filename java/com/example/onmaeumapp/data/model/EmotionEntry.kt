package com.example.onmaeumapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a single emotion entry in the diary.
 * @property id Unique identifier for the entry
 * @property date When the emotion was recorded
 * @property emotionType The type of emotion
 * @property stressLevel Stress level from 1 (lowest) to 5 (highest)
 * @property content Additional notes about the emotion
 * @property meditationCompleted Whether meditation was completed for this entry
 */
@Entity(tableName = "emotion_entries")
data class EmotionEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val emotionType: EmotionType,
    val stressLevel: Int,
    val content: String,
    val text: String = "",
    val meditationCompleted: Boolean = false
) {
    init {
        require(stressLevel in 1..5) { "Stress level must be between 1 and 5" }
        require(content.isNotEmpty()) { "Content cannot be empty" }
    }
} 
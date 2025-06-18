package com.example.onmaeumapp.backup

import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.model.MeditationEntry
import com.google.gson.annotations.SerializedName
import java.util.*

data class BackupData(
    @SerializedName("version")
    val version: Int = CURRENT_VERSION,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("entries")
    val entries: List<BackupEntry>,
    @SerializedName("meditation_entries")
    val meditationEntries: List<BackupMeditationEntry> = emptyList()
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

data class BackupEntry(
    @SerializedName("id")
    val id: Long,
    @SerializedName("date")
    val date: Date,
    @SerializedName("emotion_type")
    val emotionType: String,
    @SerializedName("stress_level")
    val stressLevel: Int,
    @SerializedName("content")
    val content: String
) {
    companion object {
        fun fromEmotionEntry(entry: EmotionEntry): BackupEntry {
            return BackupEntry(
                id = entry.id,
                date = entry.date,
                emotionType = entry.emotionType.name,
                stressLevel = entry.stressLevel,
                content = entry.text
            )
        }

        fun toEmotionEntry(entry: BackupEntry): EmotionEntry {
            return EmotionEntry(
                id = entry.id,
                date = entry.date,
                emotionType = com.example.onmaeumapp.data.model.EmotionType.valueOf(entry.emotionType),
                stressLevel = entry.stressLevel,
                content = entry.content,
                text = entry.content
            )
        }
    }
}

data class BackupMeditationEntry(
    @SerializedName("id")
    val id: Long,
    @SerializedName("date")
    val date: Date,
    @SerializedName("meditation_content_id")
    val meditationContentId: Long,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("completed")
    val completed: Boolean,
    @SerializedName("notes")
    val notes: String
) {
    companion object {
        fun fromMeditationEntry(entry: MeditationEntry): BackupMeditationEntry {
            return BackupMeditationEntry(
                id = entry.id,
                date = entry.date,
                meditationContentId = entry.meditationContentId,
                duration = entry.duration,
                completed = entry.completed,
                notes = entry.notes
            )
        }

        fun toMeditationEntry(entry: BackupMeditationEntry): MeditationEntry {
            return MeditationEntry(
                id = entry.id,
                date = entry.date,
                meditationContentId = entry.meditationContentId,
                duration = entry.duration,
                completed = entry.completed,
                notes = entry.notes
            )
        }
    }
} 
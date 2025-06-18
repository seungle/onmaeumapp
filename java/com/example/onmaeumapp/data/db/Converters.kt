package com.example.onmaeumapp.data.db

import androidx.room.TypeConverter
import com.example.onmaeumapp.data.model.EmotionType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEmotionType(value: EmotionType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEmotionType(value: String?): EmotionType? {
        return try {
            value?.let { EmotionType.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
} 
package com.example.onmaeumapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onmaeumapp.data.dao.EmotionEntryDao
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.util.Converters

@Database(
    entities = [EmotionEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emotionEntryDao(): EmotionEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "onmaeum_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
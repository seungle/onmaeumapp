package com.example.onmaeumapp.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val isKoreanLanguage: Boolean = true,
    val isAutoBackup: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    fun getUserPreferences(): Flow<UserPreferences> = flow {
        emit(
            UserPreferences(
                isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false),
                isKoreanLanguage = sharedPreferences.getBoolean("is_korean_language", true),
                isAutoBackup = sharedPreferences.getBoolean("is_auto_backup", false)
            )
        )
    }

    suspend fun updateUserPreferences(
        isDarkMode: Boolean,
        isKoreanLanguage: Boolean,
        isAutoBackup: Boolean
    ) {
        sharedPreferences.edit().apply {
            putBoolean("is_dark_mode", isDarkMode)
            putBoolean("is_korean_language", isKoreanLanguage)
            putBoolean("is_auto_backup", isAutoBackup)
            apply()
        }
    }
} 
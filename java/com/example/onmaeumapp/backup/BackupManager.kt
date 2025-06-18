package com.example.onmaeumapp.backup

import android.content.Context
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(
    private val context: Context,
    private val emotionRepository: EmotionRepository
) {
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    suspend fun createBackup(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val entries = emotionRepository.getAllEntries().first()
            val backupData = BackupData(
                entries = entries.map { BackupEntry.fromEmotionEntry(it) }
            )

            val backupFile = createBackupFile()
            FileWriter(backupFile).use { writer ->
                gson.toJson(backupData, writer)
            }

            Result.success(backupFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreBackup(backupPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupFile = File(backupPath)
            if (!backupFile.exists()) {
                return@withContext Result.failure(Exception("Backup file not found"))
            }

            val backupData = FileReader(backupFile).use { reader ->
                gson.fromJson(reader, BackupData::class.java)
            }

            // 버전 체크
            if (backupData.version > BackupData.CURRENT_VERSION) {
                return@withContext Result.failure(Exception("Unsupported backup version"))
            }

            // 기존 데이터 삭제
            emotionRepository.deleteAllEmotions()

            // 백업 데이터 복원
            val entries = backupData.entries.map { BackupEntry.toEmotionEntry(it) }
            entries.forEach { entry ->
                emotionRepository.insertEntry(entry)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createBackupFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val backupDir = File(context.filesDir, "backups").apply { mkdirs() }
        return File(backupDir, "backup_$timestamp.json")
    }

    fun getBackupFiles(): List<BackupFile> {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) return emptyList()

        return backupDir.listFiles { file ->
            file.name.startsWith("backup_") && file.name.endsWith(".json")
        }?.map { file ->
            BackupFile(
                path = file.absolutePath,
                name = file.name,
                size = file.length(),
                timestamp = file.lastModified()
            )
        }?.sortedByDescending { it.timestamp } ?: emptyList()
    }

    fun deleteBackup(backupPath: String): Boolean {
        return File(backupPath).delete()
    }
}

data class BackupFile(
    val path: String,
    val name: String,
    val size: Long,
    val timestamp: Long
) 
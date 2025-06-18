package com.example.onmaeumapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.model.MeditationContent
import com.example.onmaeumapp.data.model.MeditationEntry
import com.example.onmaeumapp.data.model.NotificationSettings
import java.time.LocalTime
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.repository.MeditationRepository
import com.example.onmaeumapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupRestoreViewModel(
    private val emotionRepository: EmotionRepository,
    private val meditationRepository: MeditationRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupRestoreUiState>(BackupRestoreUiState.Initial)
    val uiState: StateFlow<BackupRestoreUiState> = _uiState.asStateFlow()

    fun createBackup(backupFile: File) {
        viewModelScope.launch {
            _uiState.value = BackupRestoreUiState.Loading("백업 파일을 생성하는 중...")
            try {
                // 감정 데이터 백업
                val emotions = emotionRepository.getAllEmotions()
                
                // 명상 데이터 백업
                val meditations = meditationRepository.getAllMeditations()
                
                // 알림 설정 백업
                val notificationSettings = notificationRepository.getNotificationSettings()
                
                // 백업 데이터 생성
                val backupData = BackupData(
                    emotions = emotions,
                    meditations = meditations,
                    notificationSettings = notificationSettings,
                    backupDate = LocalDateTime.now()
                )
                
                // JSON으로 변환하여 파일에 저장
                backupFile.writeText(backupData.toJson())
                
                _uiState.value = BackupRestoreUiState.Success("백업이 완료되었습니다.")
            } catch (e: Exception) {
                _uiState.value = BackupRestoreUiState.Error(e.message ?: "백업 중 오류가 발생했습니다.")
            }
        }
    }

    fun restoreFromBackup(backupFile: File) {
        viewModelScope.launch {
            _uiState.value = BackupRestoreUiState.Loading("백업 파일을 복원하는 중...")
            try {
                // 백업 파일 읽기
                val backupData = BackupData.fromJson(backupFile.readText())
                
                // 기존 데이터 삭제
                emotionRepository.deleteAllEmotions()
                meditationRepository.deleteAllMeditations()
                
                // 데이터 복원
                emotionRepository.insertEmotions(backupData.emotions)
                meditationRepository.insertMeditations(backupData.meditations)
                notificationRepository.updateNotificationSettings(
                    isEnabled = backupData.notificationSettings.isEnabled,
                    time = backupData.notificationSettings.time,
                    days = backupData.notificationSettings.days
                )
                
                _uiState.value = BackupRestoreUiState.Success("복원이 완료되었습니다.")
            } catch (e: Exception) {
                _uiState.value = BackupRestoreUiState.Error(e.message ?: "복원 중 오류가 발생했습니다.")
            }
        }
    }

    fun resetState() {
        _uiState.value = BackupRestoreUiState.Initial
    }

    class Factory(
        private val emotionRepository: EmotionRepository,
        private val meditationRepository: MeditationRepository,
        private val notificationRepository: NotificationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BackupRestoreViewModel(
                emotionRepository,
                meditationRepository,
                notificationRepository
            ) as T
        }
    }
}

sealed class BackupRestoreUiState {
    object Initial : BackupRestoreUiState()
    data class Loading(val message: String) : BackupRestoreUiState()
    data class Success(val message: String) : BackupRestoreUiState()
    data class Error(val message: String) : BackupRestoreUiState()
}

data class BackupData(
    val emotions: List<EmotionEntry>,
    val meditations: List<MeditationEntry>,
    val notificationSettings: NotificationSettings,
    val backupDate: LocalDateTime
) {
    fun toJson(): String {
        // TODO: 실제 JSON 변환 구현
        return ""
    }

    companion object {
        fun fromJson(json: String): BackupData {
            // TODO: 실제 JSON 파싱 구현
            return BackupData(
                emotions = emptyList(),
                meditations = emptyList(),
                notificationSettings = NotificationSettings(
                    isEnabled = false,
                    time = LocalTime.now(),
                    days = emptySet()
                ),
                backupDate = LocalDateTime.now()
            )
        }
    }
} 
package com.example.onmaeumapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime

class NotificationSettingsViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationSettingsUiState>(NotificationSettingsUiState.Loading)
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = notificationRepository.getNotificationSettings()
                _uiState.value = NotificationSettingsUiState.Success(
                    isEnabled = settings.isEnabled,
                    time = settings.time,
                    days = settings.days
                )
            } catch (e: Exception) {
                _uiState.value = NotificationSettingsUiState.Error(e.message ?: "알림 설정을 불러오는데 실패했습니다.")
            }
        }
    }

    fun updateNotificationEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? NotificationSettingsUiState.Success
                if (currentState != null) {
                    notificationRepository.updateNotificationSettings(
                        isEnabled = isEnabled,
                        time = currentState.time,
                        days = currentState.days
                    )
                    _uiState.value = currentState.copy(isEnabled = isEnabled)
                }
            } catch (e: Exception) {
                _uiState.value = NotificationSettingsUiState.Error(e.message ?: "알림 설정을 업데이트하는데 실패했습니다.")
            }
        }
    }

    fun updateNotificationTime(time: LocalTime) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? NotificationSettingsUiState.Success
                if (currentState != null) {
                    notificationRepository.updateNotificationSettings(
                        isEnabled = currentState.isEnabled,
                        time = time,
                        days = currentState.days
                    )
                    _uiState.value = currentState.copy(time = time)
                }
            } catch (e: Exception) {
                _uiState.value = NotificationSettingsUiState.Error(e.message ?: "알림 시간을 업데이트하는데 실패했습니다.")
            }
        }
    }

    fun updateNotificationDays(days: Set<Int>) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? NotificationSettingsUiState.Success
                if (currentState != null) {
                    notificationRepository.updateNotificationSettings(
                        isEnabled = currentState.isEnabled,
                        time = currentState.time,
                        days = days
                    )
                    _uiState.value = currentState.copy(days = days)
                }
            } catch (e: Exception) {
                _uiState.value = NotificationSettingsUiState.Error(e.message ?: "알림 요일을 업데이트하는데 실패했습니다.")
            }
        }
    }

    class Factory(
        private val notificationRepository: NotificationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationSettingsViewModel(notificationRepository) as T
        }
    }
}

sealed class NotificationSettingsUiState {
    object Loading : NotificationSettingsUiState()
    data class Success(
        val isEnabled: Boolean,
        val time: LocalTime,
        val days: Set<Int>
    ) : NotificationSettingsUiState()
    data class Error(val message: String) : NotificationSettingsUiState()
} 
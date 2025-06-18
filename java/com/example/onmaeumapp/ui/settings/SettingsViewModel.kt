package com.example.onmaeumapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.repository.UserPreferencesRepository
import com.example.onmaeumapp.data.repository.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.getUserPreferences().collect { preferences ->
                    _uiState.value = SettingsUiState.Success(
                        isDarkMode = preferences.isDarkMode,
                        isKoreanLanguage = preferences.isKoreanLanguage,
                        isAutoBackup = preferences.isAutoBackup
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "설정을 불러오는데 실패했습니다.")
            }
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? SettingsUiState.Success
                if (currentState != null) {
                    userPreferencesRepository.updateUserPreferences(
                        isDarkMode = isDarkMode,
                        isKoreanLanguage = currentState.isKoreanLanguage,
                        isAutoBackup = currentState.isAutoBackup
                    )
                    _uiState.value = currentState.copy(isDarkMode = isDarkMode)
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "다크 모드 설정을 업데이트하는데 실패했습니다.")
            }
        }
    }

    fun updateLanguage(isKoreanLanguage: Boolean) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? SettingsUiState.Success
                if (currentState != null) {
                    userPreferencesRepository.updateUserPreferences(
                        isDarkMode = currentState.isDarkMode,
                        isKoreanLanguage = isKoreanLanguage,
                        isAutoBackup = currentState.isAutoBackup
                    )
                    _uiState.value = currentState.copy(isKoreanLanguage = isKoreanLanguage)
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "언어 설정을 업데이트하는데 실패했습니다.")
            }
        }
    }

    fun updateAutoBackup(isAutoBackup: Boolean) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? SettingsUiState.Success
                if (currentState != null) {
                    userPreferencesRepository.updateUserPreferences(
                        isDarkMode = currentState.isDarkMode,
                        isKoreanLanguage = currentState.isKoreanLanguage,
                        isAutoBackup = isAutoBackup
                    )
                    _uiState.value = currentState.copy(isAutoBackup = isAutoBackup)
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "자동 백업 설정을 업데이트하는데 실패했습니다.")
            }
        }
    }

    class Factory(
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(userPreferencesRepository) as T
        }
    }
}

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(
        val isDarkMode: Boolean,
        val isKoreanLanguage: Boolean,
        val isAutoBackup: Boolean
    ) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
} 
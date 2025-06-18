package com.example.onmaeumapp.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class DiaryViewModel(
    private val emotionRepository: EmotionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiaryUiState>(DiaryUiState.Initial)
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    fun saveDiaryEntry(text: String, emotionType: EmotionType, stressLevel: Int) {
        viewModelScope.launch {
            _uiState.value = DiaryUiState.Loading
            
            try {
                val entry = EmotionEntry(
                    date = Date(),
                    emotionType = emotionType,
                    stressLevel = stressLevel,
                    content = "",
                    text = text
                )
                emotionRepository.insertEntry(entry)
                _uiState.value = DiaryUiState.Success
            } catch (e: Exception) {
                _uiState.value = DiaryUiState.Error(e.message ?: "일기 저장에 실패했습니다.")
            }
        }
    }

    class Factory(
        private val emotionRepository: EmotionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiaryViewModel(emotionRepository) as T
        }
    }
}

sealed class DiaryUiState {
    object Initial : DiaryUiState()
    object Loading : DiaryUiState()
    object Success : DiaryUiState()
    data class Error(val message: String) : DiaryUiState()
} 
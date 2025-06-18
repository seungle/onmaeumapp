package com.example.onmaeumapp.ui.stress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.model.MeditationContent
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.data.repository.MeditationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.util.Date

class StressAnalysisViewModel(
    private val emotionRepository: EmotionRepository,
    private val meditationRepository: MeditationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StressAnalysisUiState>(StressAnalysisUiState.Initial)
    val uiState: StateFlow<StressAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeEmotion(text: String, emotionType: EmotionType) {
        viewModelScope.launch {
            _uiState.value = StressAnalysisUiState.Loading
            
            try {
                // 감정 일기 저장
                val entry = EmotionEntry(
                    date = Date(),
                    emotionType = emotionType,
                    stressLevel = calculateStressLevel(emotionType),
                    content = "",
                    text = text
                )
                emotionRepository.insertEntry(entry)

                // 명상 추천 가져오기
                val recommendedMeditations = meditationRepository.getContentsByCategory(
                    getCategoryForEmotion(emotionType)
                ).first()

                _uiState.value = StressAnalysisUiState.Success(
                    stressLevel = entry.stressLevel,
                    recommendedMeditations = recommendedMeditations
                )
            } catch (e: Exception) {
                _uiState.value = StressAnalysisUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun calculateStressLevel(emotionType: EmotionType): Int {
        return when (emotionType) {
            EmotionType.HAPPY -> 1
            EmotionType.CALM -> 2
            EmotionType.NEUTRAL -> 3
            EmotionType.ANXIOUS -> 4
            EmotionType.SAD -> 4
            EmotionType.ANGRY -> 5
        }
    }

    private fun getCategoryForEmotion(emotionType: EmotionType): String {
        return when (emotionType) {
            EmotionType.HAPPY -> "행복"
            EmotionType.CALM -> "평온"
            EmotionType.NEUTRAL -> "일반"
            EmotionType.ANXIOUS -> "불안"
            EmotionType.SAD -> "슬픔"
            EmotionType.ANGRY -> "분노"
        }
    }

    class Factory(
        private val emotionRepository: EmotionRepository,
        private val meditationRepository: MeditationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StressAnalysisViewModel(emotionRepository, meditationRepository) as T
        }
    }
}

sealed class StressAnalysisUiState {
    object Initial : StressAnalysisUiState()
    object Loading : StressAnalysisUiState()
    data class Success(
        val stressLevel: Int,
        val recommendedMeditations: List<MeditationContent>
    ) : StressAnalysisUiState()
    data class Error(val message: String) : StressAnalysisUiState()
} 
package com.example.onmaeumapp.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.example.onmaeumapp.data.model.MeditationContent
import com.example.onmaeumapp.data.model.EmotionType
import com.example.onmaeumapp.data.repository.MeditationRepository
import com.example.onmaeumapp.data.repository.EmotionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class Meditation(
    val id: Long = 0,
    val title: String,
    val description: String,
    val duration: Int,
    val category: String,
    val difficulty: String,
    val audioUrl: String,
    val imageUrl: String
)

data class MeditationUiState(
    val meditation: Meditation? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlaying: Boolean = false
)

class MeditationViewModel(
    private val meditationRepository: MeditationRepository,
    private val emotionRepository: EmotionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()

    init {
        loadMeditation()
    }

    private fun loadMeditation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val meditationId = savedStateHandle.get<Int>("meditationId") ?: return@launch
                val meditation = meditationRepository.getMeditationById(meditationId)
                _uiState.value = _uiState.value.copy(
                    meditation = meditation,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun togglePlayPause() {
        _uiState.value = _uiState.value.copy(
            isPlaying = !_uiState.value.isPlaying
        )
    }

    fun startMeditation() {
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun stopMeditation() {
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun loadMeditations(emotionType: EmotionType? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val meditations = if (emotionType != null) {
                    meditationRepository.getContentsByCategory(getCategoryForEmotion(emotionType))
                } else {
                    meditationRepository.getAllContents()
                }
                
                meditations.collect { contents ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "명상 콘텐츠를 불러오는데 실패했습니다."
                )
            }
        }
    }

    private fun getCategoryForEmotion(emotionType: EmotionType): String {
        return when (emotionType) {
            EmotionType.HAPPY -> "행복"
            EmotionType.CALM -> "차분"
            EmotionType.NEUTRAL -> "중립"
            EmotionType.ANXIOUS -> "불안"
            EmotionType.SAD -> "슬픔"
            EmotionType.ANGRY -> "분노"
            EmotionType.TIRED -> "피로"
        }
    }

    class Factory(
        private val meditationRepository: MeditationRepository,
        private val emotionRepository: EmotionRepository,
        private val savedStateHandle: SavedStateHandle
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MeditationViewModel(meditationRepository, emotionRepository, savedStateHandle) as T
        }
    }
} 
package com.example.onmaeumapp.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.model.MeditationContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MeditationDetailViewModel(
    private val meditation: MeditationContent
) : ViewModel() {

    private val _uiState = MutableStateFlow<MeditationDetailUiState>(MeditationDetailUiState.Initial)
    val uiState: StateFlow<MeditationDetailUiState> = _uiState.asStateFlow()

    private var remainingTimeInSeconds = meditation.duration * 60L
    private var isPlaying = false

    fun togglePlayPause() {
        isPlaying = !isPlaying
        if (isPlaying) {
            startTimer()
        } else {
            _uiState.value = MeditationDetailUiState.Paused(
                meditation = meditation,
                remainingTimeInSeconds = remainingTimeInSeconds
            )
        }
    }

    fun reset() {
        isPlaying = false
        remainingTimeInSeconds = meditation.duration * 60L
        _uiState.value = MeditationDetailUiState.Initial
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (isPlaying && remainingTimeInSeconds > 0) {
                _uiState.value = MeditationDetailUiState.Playing(
                    meditation = meditation,
                    remainingTimeInSeconds = remainingTimeInSeconds
                )
                delay(1000)
                remainingTimeInSeconds--
            }
            
            if (remainingTimeInSeconds <= 0) {
                isPlaying = false
                _uiState.value = MeditationDetailUiState.Completed(meditation)
            }
        }
    }

    fun formatTime(seconds: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    class Factory(
        private val meditation: MeditationContent
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MeditationDetailViewModel(meditation) as T
        }
    }
}

sealed class MeditationDetailUiState {
    object Initial : MeditationDetailUiState()
    data class Playing(
        val meditation: MeditationContent,
        val remainingTimeInSeconds: Long
    ) : MeditationDetailUiState()
    data class Paused(
        val meditation: MeditationContent,
        val remainingTimeInSeconds: Long
    ) : MeditationDetailUiState()
    data class Completed(val meditation: MeditationContent) : MeditationDetailUiState()
} 
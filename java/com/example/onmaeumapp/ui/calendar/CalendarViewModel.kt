package com.example.onmaeumapp.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onmaeumapp.data.model.EmotionEntry
import com.example.onmaeumapp.data.repository.EmotionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class CalendarViewModel(
    private val emotionRepository: EmotionRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _entries = MutableStateFlow<Map<LocalDate, EmotionEntry>>(emptyMap())
    val entries: StateFlow<Map<LocalDate, EmotionEntry>> = _entries.asStateFlow()

    init {
        loadEntries()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun loadEntries() {
        viewModelScope.launch {
            emotionRepository.getAllEntries()
                .collect { entries ->
                    val entryMap = entries.associate { entry ->
                        entry.date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate() to entry
                    }
                    _entries.value = entryMap
                }
        }
    }

    fun getEntryForDate(date: LocalDate): EmotionEntry? {
        return _entries.value[date]
    }

    class Factory(
        private val emotionRepository: EmotionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(emotionRepository) as T
        }
    }
} 
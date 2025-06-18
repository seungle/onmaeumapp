package com.example.onmaeumapp.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.onmaeumapp.R
import com.example.onmaeumapp.data.model.EmotionEntry
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import com.example.onmaeumapp.ui.theme.OnmaeumAppTheme
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.remember
import com.example.onmaeumapp.util.FakeEmotionRepository

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val entries by viewModel.entries.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        MonthSelector(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )

        CalendarGrid(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            entries = entries,
            onDateSelected = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectedDateInfo(
            date = selectedDate,
            entry = viewModel.getEntryForDate(selectedDate)
        )
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Text("<")
        }
        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onNextMonth) {
            Text(">")
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    entries: Map<LocalDate, EmotionEntry>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val daysInMonth = lastDayOfMonth.dayOfMonth

    Column {
        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 달력 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 첫 주의 빈 칸
            items((1 until firstDayOfWeek).toList()) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // 날짜 칸
            items((1..daysInMonth).toList()) { day ->
                val date = currentMonth.atDay(day)
                val entry = entries[date]
                val isSelected = date == selectedDate

                CalendarDay(
                    date = date,
                    entry = entry,
                    isSelected = isSelected,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    entry: EmotionEntry?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    entry != null -> getEmotionColor(entry.emotionType).copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SelectedDateInfo(
    date: LocalDate,
    entry: EmotionEntry?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
                style = MaterialTheme.typography.titleMedium
            )

            if (entry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "감정: ${getEmotionText(entry.emotionType)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "스트레스 레벨: ${entry.stressLevel}",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (entry.text.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "기록된 감정이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getEmotionColor(emotionType: EmotionType): Color {
    return when (emotionType) {
        EmotionType.HAPPY -> Color(0xFFFFD700) // 노란색
        EmotionType.CALM -> Color(0xFF90CAF9) // 파란색
        EmotionType.NEUTRAL -> Color(0xFFBDBDBD) // 회색
        EmotionType.ANXIOUS -> Color(0xFFFFB74D) // 주황색
        EmotionType.SAD -> Color(0xFF64B5F6) // 하늘색
        EmotionType.ANGRY -> Color(0xFFEF5350) // 빨간색
    }
}

private fun getEmotionText(emotionType: EmotionType): String {
    return when (emotionType) {
        EmotionType.HAPPY -> "행복해요"
        EmotionType.CALM -> "차분해요"
        EmotionType.NEUTRAL -> "보통이에요"
        EmotionType.ANXIOUS -> "불안해요"
        EmotionType.SAD -> "슬퍼요"
        EmotionType.ANGRY -> "화나요"
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun CalendarScreenLightPreview() {
    val fakeRepository = remember { FakeEmotionRepository() }
    val viewModel = remember { CalendarViewModel(emotionRepository = fakeRepository) }
    
    OnmaeumAppTheme(darkTheme = false) {
        CalendarScreen(viewModel = viewModel)
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun CalendarScreenDarkPreview() {
    val fakeRepository = remember { FakeEmotionRepository() }
    val viewModel = remember { CalendarViewModel(emotionRepository = fakeRepository) }
    
    OnmaeumAppTheme(darkTheme = true) {
        CalendarScreen(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val fakeRepository = remember { FakeEmotionRepository() }
    val viewModel = remember { CalendarViewModel(emotionRepository = fakeRepository) }

    OnmaeumAppTheme {
        CalendarScreen(viewModel = viewModel)
    }
} 
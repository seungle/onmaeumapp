package com.example.onmaeumapp.ui.diary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onmaeumapp.data.repository.EmotionRepository
import com.example.onmaeumapp.ui.theme.OnmaeumAppTheme
import com.example.onmaeumapp.util.FakeEmotionRepository
import com.example.onmaeumapp.util.FakeMeditationRepository

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    onDiarySaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf<EmotionType?>(null) }
    var stressLevel by remember { mutableStateOf(3) }

    LaunchedEffect(uiState) {
        if (uiState is DiaryUiState.Success) {
            onDiarySaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "오늘 하루를 기록해보세요",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        EmotionSelectionButtons(
            selectedEmotion = selectedEmotion,
            onEmotionSelected = { selectedEmotion = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StressLevelSelector(
            stressLevel = stressLevel,
            onStressLevelChanged = { stressLevel = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("오늘 하루는 어땠나요?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedEmotion?.let { emotion ->
                    viewModel.saveDiaryEntry(text, emotion, stressLevel)
                }
            },
            enabled = selectedEmotion != null && text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장하기")
        }

        when (uiState) {
            is DiaryUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is DiaryUiState.Error -> {
                Text(
                    text = (uiState as DiaryUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun EmotionSelectionButtons(
    selectedEmotion: EmotionType?,
    onEmotionSelected: (EmotionType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmotionType.values().forEach { emotion ->
            EmotionButton(
                emotion = emotion,
                isSelected = emotion == selectedEmotion,
                onClick = { onEmotionSelected(emotion) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EmotionButton(
    emotion: EmotionType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Text(
            text = when (emotion) {
                EmotionType.HAPPY -> "행복해요"
                EmotionType.CALM -> "차분해요"
                EmotionType.NEUTRAL -> "보통이에요"
                EmotionType.ANXIOUS -> "불안해요"
                EmotionType.SAD -> "슬퍼요"
                EmotionType.ANGRY -> "화나요"
            }
        )
    }
}

@Composable
private fun StressLevelSelector(
    stressLevel: Int,
    onStressLevelChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "스트레스 레벨: $stressLevel",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = stressLevel.toFloat(),
            onValueChange = { onStressLevelChanged(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("매우 낮음")
            Text("보통")
            Text("매우 높음")
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun DiaryScreenLightPreview() {
    OnmaeumAppTheme(darkTheme = false) {
        val fakeEmotionRepository = remember { FakeEmotionRepository() }
        val fakeMeditationRepository = remember { FakeMeditationRepository() }
        val viewModel = remember { 
            DiaryViewModel(
                emotionRepository = fakeEmotionRepository,
                meditationRepository = fakeMeditationRepository
            )
        }
        DiaryScreen(
            viewModel = viewModel,
            onDiarySaved = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun DiaryScreenDarkPreview() {
    OnmaeumAppTheme(darkTheme = true) {
        val fakeEmotionRepository = remember { FakeEmotionRepository() }
        val fakeMeditationRepository = remember { FakeMeditationRepository() }
        val viewModel = remember { 
            DiaryViewModel(
                emotionRepository = fakeEmotionRepository,
                meditationRepository = fakeMeditationRepository
            )
        }
        DiaryScreen(
            viewModel = viewModel,
            onDiarySaved = {}
        )
    }
} 
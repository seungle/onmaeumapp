package com.example.onmaeumapp.ui.stress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.onmaeumapp.R
import com.example.onmaeumapp.data.model.MeditationContent

@Composable
fun StressAnalysisScreen(
    viewModel: StressAnalysisViewModel,
    onMeditationClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf<EmotionType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "오늘 하루는 어떠셨나요?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        EmotionSelectionButtons(
            selectedEmotion = selectedEmotion,
            onEmotionSelected = { selectedEmotion = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("감정을 자세히 설명해주세요") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedEmotion?.let { emotion ->
                    viewModel.analyzeEmotion(text, emotion)
                }
            },
            enabled = selectedEmotion != null && text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("분석하기")
        }

        when (uiState) {
            is StressAnalysisUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is StressAnalysisUiState.Success -> {
                val successState = uiState as StressAnalysisUiState.Success
                StressLevelIndicator(successState.stressLevel)
                RecommendedMeditations(
                    meditations = successState.recommendedMeditations,
                    onMeditationClick = onMeditationClick
                )
            }
            is StressAnalysisUiState.Error -> {
                Text(
                    text = (uiState as StressAnalysisUiState.Error).message,
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
private fun StressLevelIndicator(stressLevel: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "스트레스 레벨: $stressLevel",
            style = MaterialTheme.typography.titleLarge
        )
        LinearProgressIndicator(
            progress = stressLevel / 5f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendedMeditations(
    meditations: List<MeditationContent>,
    onMeditationClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "추천 명상",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        meditations.forEach { meditation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = { onMeditationClick(meditation.id) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = meditation.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = meditation.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${meditation.duration}분",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
} 
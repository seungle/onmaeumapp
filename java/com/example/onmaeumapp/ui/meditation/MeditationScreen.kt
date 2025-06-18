package com.example.onmaeumapp.ui.meditation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.onmaeumapp.data.model.MeditationContent
import com.example.onmaeumapp.ui.theme.OnmaeumAppTheme
import androidx.compose.ui.tooling.preview.Preview
import com.example.onmaeumapp.util.FakeMeditationRepository

@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel,
    onMeditationSelected: (MeditationContent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedEmotion by remember { mutableStateOf<EmotionType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "명상",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        EmotionFilter(
            selectedEmotion = selectedEmotion,
            onEmotionSelected = { emotion ->
                selectedEmotion = emotion
                viewModel.loadMeditations(emotion)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is MeditationUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MeditationUiState.Success -> {
                val meditations = (uiState as MeditationUiState.Success).meditations
                if (meditations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "추천된 명상이 없습니다",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(meditations) { meditation ->
                            MeditationCard(
                                meditation = meditation,
                                onClick = { onMeditationSelected(meditation) }
                            )
                        }
                    }
                }
            }
            is MeditationUiState.Error -> {
                Text(
                    text = (uiState as MeditationUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun EmotionFilter(
    selectedEmotion: EmotionType?,
    onEmotionSelected: (EmotionType) -> Unit
) {
    Column {
        Text(
            text = "감정별 명상",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmotionType.values().forEach { emotion ->
                EmotionChip(
                    emotion = emotion,
                    isSelected = emotion == selectedEmotion,
                    onClick = { onEmotionSelected(emotion) }
                )
            }
        }
    }
}

@Composable
private fun EmotionChip(
    emotion: EmotionType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Text(
            text = getEmotionText(emotion),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun MeditationCard(
    meditation: MeditationContent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = meditation.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = meditation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${meditation.duration}분",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = meditation.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
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
fun MeditationScreenLightPreview() {
    OnmaeumAppTheme(darkTheme = false) {
        val fakeRepository = remember { FakeMeditationRepository() }
        val viewModel = remember { MeditationViewModel(meditationRepository = fakeRepository) }
        MeditationScreen(
            viewModel = viewModel,
            onMeditationSelected = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun MeditationScreenDarkPreview() {
    OnmaeumAppTheme(darkTheme = true) {
        val fakeRepository = remember { FakeMeditationRepository() }
        val viewModel = remember { MeditationViewModel(meditationRepository = fakeRepository) }
        MeditationScreen(
            viewModel = viewModel,
            onMeditationSelected = {}
        )
    }
} 
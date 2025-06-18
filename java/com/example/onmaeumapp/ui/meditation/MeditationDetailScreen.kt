package com.example.onmaeumapp.ui.meditation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import com.example.onmaeumapp.data.model.MeditationContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationDetailScreen(
    viewModel: MeditationDetailViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("명상") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is MeditationDetailUiState.Initial -> {
                    Text("명상을 시작하려면 버튼을 눌러주세요")
                    Spacer(modifier = Modifier.height(32.dp))
                    PlayButton(onClick = viewModel::togglePlayPause)
                }
                is MeditationDetailUiState.Playing -> {
                    MeditationInfo(meditation = state.meditation)
                    Spacer(modifier = Modifier.height(32.dp))
                    TimerDisplay(
                        remainingTime = state.remainingTimeInSeconds,
                        formatTime = viewModel::formatTime
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ControlButtons(
                        isPlaying = true,
                        onPlayPause = viewModel::togglePlayPause,
                        onReset = viewModel::reset
                    )
                }
                is MeditationDetailUiState.Paused -> {
                    MeditationInfo(meditation = state.meditation)
                    Spacer(modifier = Modifier.height(32.dp))
                    TimerDisplay(
                        remainingTime = state.remainingTimeInSeconds,
                        formatTime = viewModel::formatTime
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ControlButtons(
                        isPlaying = false,
                        onPlayPause = viewModel::togglePlayPause,
                        onReset = viewModel::reset
                    )
                }
                is MeditationDetailUiState.Completed -> {
                    MeditationInfo(meditation = state.meditation)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "명상이 완료되었습니다",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = viewModel::reset,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("다시 시작하기")
                    }
                }
            }
        }
    }
}

@Composable
private fun MeditationInfo(meditation: MeditationContent) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = meditation.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = meditation.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "${meditation.duration}분",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TimerDisplay(
    remainingTime: Long,
    formatTime: (Long) -> String
) {
    Text(
        text = formatTime(remainingTime),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "재생",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ControlButtons(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onReset,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "초기화",
                modifier = Modifier.size(24.dp)
            )
        }
        
        Button(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            shape = CircleShape
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "일시정지" else "재생",
                modifier = Modifier.size(32.dp)
            )
        }
    }
} 
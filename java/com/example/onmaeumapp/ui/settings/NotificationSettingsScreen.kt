package com.example.onmaeumapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림 설정") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is NotificationSettingsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NotificationSettingsUiState.Success -> {
                NotificationSettingsContent(
                    isEnabled = state.isEnabled,
                    time = state.time,
                    days = state.days,
                    onEnabledChanged = viewModel::updateNotificationEnabled,
                    onTimeChanged = viewModel::updateNotificationTime,
                    onDaysChanged = viewModel::updateNotificationDays
                )
            }
            is NotificationSettingsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsContent(
    isEnabled: Boolean,
    time: LocalTime,
    days: Set<Int>,
    onEnabledChanged: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDaysChanged: (Set<Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 알림 활성화 스위치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "알림 받기",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onEnabledChanged
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 알림 시간 선택
        Text(
            text = "알림 시간",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TimePickerButton(
            time = time,
            onTimeSelected = onTimeChanged,
            enabled = isEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 요일 선택
        Text(
            text = "알림 요일",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DaySelector(
            selectedDays = days,
            onDaysSelected = onDaysChanged,
            enabled = isEnabled
        )
    }
}

@Composable
private fun TimePickerButton(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    enabled: Boolean
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    Button(
        onClick = { showTimePicker = true },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.titleMedium
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { 
                onTimeSelected(it)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaySelector(
    selectedDays: Set<Int>,
    onDaysSelected: (Set<Int>) -> Unit,
    enabled: Boolean
) {
    val days = listOf("일", "월", "화", "수", "목", "금", "토")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, day ->
            FilterChip(
                selected = index in selectedDays,
                onClick = {
                    if (enabled) {
                        val newDays = selectedDays.toMutableSet()
                        if (index in selectedDays) {
                            newDays.remove(index)
                        } else {
                            newDays.add(index)
                        }
                        onDaysSelected(newDays)
                    }
                },
                label = { Text(day) },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("시간 선택") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 시간 선택
                    Column {
                        Text("시간")
                        NumberPicker(
                            value = selectedHour,
                            onValueChange = { selectedHour = it },
                            range = 0..23
                        )
                    }
                    
                    // 분 선택
                    Column {
                        Text("분")
                        NumberPicker(
                            value = selectedMinute,
                            onValueChange = { selectedMinute = it },
                            range = 0..59
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column {
        IconButton(
            onClick = { 
                if (value < range.last) onValueChange(value + 1)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "증가")
        }
        
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium
        )
        
        IconButton(
            onClick = { 
                if (value > range.first) onValueChange(value - 1)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "감소")
        }
    }
} 
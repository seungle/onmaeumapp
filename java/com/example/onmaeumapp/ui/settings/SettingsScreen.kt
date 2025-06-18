package com.example.onmaeumapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onBackupRestoreClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SettingsUiState.Success -> {
                SettingsContent(
                    isDarkMode = state.isDarkMode,
                    isKoreanLanguage = state.isKoreanLanguage,
                    isAutoBackup = state.isAutoBackup,
                    onDarkModeChanged = viewModel::updateDarkMode,
                    onLanguageChanged = viewModel::updateLanguage,
                    onAutoBackupChanged = viewModel::updateAutoBackup,
                    onNotificationSettingsClick = onNotificationSettingsClick,
                    onBackupRestoreClick = onBackupRestoreClick
                )
            }
            is SettingsUiState.Error -> {
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
private fun SettingsContent(
    isDarkMode: Boolean,
    isKoreanLanguage: Boolean,
    isAutoBackup: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onLanguageChanged: (Boolean) -> Unit,
    onAutoBackupChanged: (Boolean) -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onBackupRestoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 테마 설정
        SettingsItem(
            title = "다크 모드",
            icon = Icons.Default.DarkMode,
            trailingContent = {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChanged
                )
            }
        )

        Divider()

        // 언어 설정
        SettingsItem(
            title = "한국어",
            icon = Icons.Default.Language,
            trailingContent = {
                Switch(
                    checked = isKoreanLanguage,
                    onCheckedChange = onLanguageChanged
                )
            }
        )

        Divider()

        // 자동 백업 설정
        SettingsItem(
            title = "자동 백업",
            icon = Icons.Default.Backup,
            trailingContent = {
                Switch(
                    checked = isAutoBackup,
                    onCheckedChange = onAutoBackupChanged
                )
            }
        )

        Divider()

        // 알림 설정
        SettingsItem(
            title = "알림 설정",
            icon = Icons.Default.Notifications,
            onClick = onNotificationSettingsClick
        )

        Divider()

        // 데이터 백업/복원
        SettingsItem(
            title = "데이터 백업/복원",
            icon = Icons.Default.Restore,
            onClick = onBackupRestoreClick
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 
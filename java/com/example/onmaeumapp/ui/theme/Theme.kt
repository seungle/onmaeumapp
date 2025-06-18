package com.example.onmaeumapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // 연두색
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9), // 연한 연두색
    onPrimaryContainer = Color(0xFF1B5E20), // 진한 연두색
    secondary = Color(0xFF81C784), // 중간 연두색
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9), // 매우 연한 연두색
    onSecondaryContainer = Color(0xFF2E7D32), // 진한 연두색
    tertiary = Color(0xFF66BB6A), // 밝은 연두색
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF1F8E9), // 연한 연두색 배경
    onTertiaryContainer = Color(0xFF1B5E20), // 진한 연두색 텍스트
    background = Color(0xFFF5F5F5), // 밝은 회색 배경
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784), // 밝은 연두색
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B5E20), // 진한 연두색
    onPrimaryContainer = Color(0xFFC8E6C9), // 연한 연두색
    secondary = Color(0xFF66BB6A), // 중간 연두색
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32), // 진한 연두색
    onSecondaryContainer = Color(0xFFE8F5E9), // 매우 연한 연두색
    tertiary = Color(0xFF4CAF50), // 기본 연두색
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF1B5E20), // 진한 연두색
    onTertiaryContainer = Color(0xFFF1F8E9), // 연한 연두색
    background = Color(0xFF121212), // 어두운 배경
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E), // 어두운 표면
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun OnmaeumAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    background = Color(0xFF252525),
    onBackground = Color(0xFFFAFAFA),
    surface = Color(0xFF252525),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF444444),
    onSurfaceVariant = Color(0xFFB5B5B5),
    outline = Color(0xFF444444),
    error = Color(0xFF9D2538),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF252525),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF252525),
    surfaceVariant = Color(0xFFECECF0),
    onSurfaceVariant = Color(0xFF717182),
    outline = Color(0x1A000000),
    error = Color(0xFFD4183D),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

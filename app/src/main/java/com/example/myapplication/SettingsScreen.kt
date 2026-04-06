package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    modifier: Modifier,
    labels: AppStrings,
    language: AppLanguage,
    theme: AppThemeMode,
    isDark: Boolean,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenTopBar(title = labels.settings, onBack = onBack)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(title = labels.language, icon = Icons.Default.Language) {
                AppCard(isDark = isDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SelectionButton(
                            text = labels.russian,
                            selected = language == AppLanguage.RU,
                            onClick = { onLanguageChange(AppLanguage.RU) },
                            isDark = isDark
                        )
                        SelectionButton(
                            text = labels.english,
                            selected = language == AppLanguage.EN,
                            onClick = { onLanguageChange(AppLanguage.EN) },
                            isDark = isDark
                        )
                    }
                }
            }

            SettingsSection(title = labels.theme, icon = Icons.Default.Settings) {
                AppCard(isDark = isDark) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SelectionButton(
                            text = labels.light,
                            selected = theme == AppThemeMode.LIGHT,
                            onClick = { onThemeChange(AppThemeMode.LIGHT) },
                            isDark = isDark
                        )
                        SelectionButton(
                            text = labels.dark,
                            selected = theme == AppThemeMode.DARK,
                            onClick = { onThemeChange(AppThemeMode.DARK) },
                            isDark = isDark
                        )
                    }
                }
            }

            SettingsSection(title = labels.about, icon = Icons.Default.NoteAlt) {
                AppCard(isDark = isDark) {
                    Text(
                        text = labels.weatherData,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• ${labels.version}",
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${labels.updated}",
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

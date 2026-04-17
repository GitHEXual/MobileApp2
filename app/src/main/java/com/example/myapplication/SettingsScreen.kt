package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        }
    }
}

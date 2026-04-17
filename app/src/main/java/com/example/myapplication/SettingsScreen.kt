package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    mapKeyRows: List<MapKeyRow>,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onSetActiveMapKey: (String) -> Unit,
    onAddMapKey: (String, String) -> Unit,
    onRemoveMapKey: (String) -> Unit
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

            SettingsSection(title = labels.mapYandexKeysSection, icon = Icons.Outlined.VpnKey) {
                MapKeysSettingsBlock(
                    labels = labels,
                    isDark = isDark,
                    rows = mapKeyRows,
                    onSetActive = onSetActiveMapKey,
                    onAdd = onAddMapKey,
                    onRemove = onRemoveMapKey
                )
            }
        }
    }
}

@Composable
private fun MapKeysSettingsBlock(
    labels: AppStrings,
    isDark: Boolean,
    rows: List<MapKeyRow>,
    onSetActive: (String) -> Unit,
    onAdd: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    var addOpen by remember { mutableStateOf(false) }
    var nameField by remember { mutableStateOf("") }
    var keyField by remember { mutableStateOf("") }

    AppCard(isDark = isDark) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = row.displayName, fontSize = 15.sp)
                        Text(
                            text = row.maskedKey,
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (row.isActive) {
                            Text(
                                text = labels.mapKeysStateActive,
                                fontSize = 11.sp,
                                color = bluePrimary
                            )
                        }
                    }
                    Column {
                        if (!row.isActive) {
                            TextButton(onClick = { onSetActive(row.id) }) {
                                Text(labels.mapKeysMakeActive)
                            }
                        }
                        TextButton(onClick = { onRemove(row.id) }) {
                            Text(labels.mapKeysRemove, color = destructive)
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    nameField = ""
                    keyField = ""
                    addOpen = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(labels.mapKeysAdd)
            }
        }
    }

    if (addOpen) {
        AlertDialog(
            onDismissRequest = { addOpen = false },
            title = { Text(labels.mapKeysAdd) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nameField,
                        onValueChange = { nameField = it },
                        label = { Text(labels.mapKeyNameLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = keyField,
                        onValueChange = { keyField = it },
                        label = { Text(labels.mapKeyValueLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (keyField.isNotBlank()) {
                            onAdd(nameField, keyField)
                            addOpen = false
                        }
                    }
                ) {
                    Text(labels.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { addOpen = false }) {
                    Text(labels.cancel)
                }
            }
        )
    }
}

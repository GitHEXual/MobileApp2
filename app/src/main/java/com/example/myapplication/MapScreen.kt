package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapScreen(
    modifier: Modifier,
    cities: List<CityCatalogItem>,
    favorites: List<FavoriteCity>,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onDeleteFavorite: (String) -> Unit,
    onAddToFavorites: (String, String?) -> Unit
) {
    var selectedFavorite by remember { mutableStateOf<FavoriteCity?>(null) }
    var selectedAvailable by remember { mutableStateOf<CityCatalogItem?>(null) }
    val favoritesById = remember(favorites) { favorites.associateBy(FavoriteCity::cityId) }

    Column(modifier = modifier.fillMaxSize()) {
        ScreenTopBar(title = labels.map, onBack = onBack)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF183657), Color(0xFF244A6A), Color(0xFF1B2B3B))
                        } else {
                            listOf(Color(0xFF95C7F5), Color(0xFF6FAADB), Color(0xFFB2D6F5))
                        }
                    )
                )
        ) {
            MapGridOverlay(isDark = isDark)
            cities.forEach { city ->
                val favorite = favoritesById[city.id]
                Marker(
                    modifier = Modifier.offset(
                        x = percent(maxWidth, city.mapPosition.xFraction) - 24.dp,
                        y = percent(maxHeight, city.mapPosition.yFraction) - 36.dp
                    ),
                    title = localizedCity(city.weather, language),
                    primary = favorite != null,
                    isDark = isDark,
                    onClick = {
                        if (favorite != null) {
                            selectedFavorite = favorite
                        } else {
                            selectedAvailable = city
                        }
                    }
                )
            }
        }
    }

    selectedFavorite?.let { favorite ->
        AlertDialog(
            onDismissRequest = { selectedFavorite = null },
            title = { Text(localizedCity(favorite.weather, language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = signedTemperature(favorite.weather.temperature, suffix = "°C"),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = localizedCondition(favorite.weather, language),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!favorite.note.isNullOrBlank()) {
                        NotePreviewCard(
                            note = favorite.note,
                            label = labels.noteLabel,
                            isDark = isDark,
                            italic = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onOpenDetail(favorite.cityId)
                        selectedFavorite = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                ) {
                    Text(labels.openDetails)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onDeleteFavorite(favorite.cityId)
                        selectedFavorite = null
                    }
                ) {
                    Text(labels.delete, color = destructive)
                }
            }
        )
    }

    selectedAvailable?.let { city ->
        NoteDialog(
            title = labels.addToFavorites,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = "",
            confirmText = labels.save,
            dismissText = labels.cancel,
            leadingContent = {
                AppCard(isDark = isDark, backgroundOverride = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(localizedCity(city.weather, language), fontWeight = FontWeight.Medium)
                            Text(
                                localizedCondition(city.weather, language),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            signedTemperature(city.weather.temperature, suffix = "°"),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            },
            onDismiss = { selectedAvailable = null },
            onConfirm = { value ->
                onAddToFavorites(city.id, value)
                selectedAvailable = null
            }
        )
    }
}

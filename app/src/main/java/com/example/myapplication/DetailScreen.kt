package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.MaterialTheme
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
fun DetailScreen(
    modifier: Modifier,
    city: CityCatalogItem,
    favorite: FavoriteCity?,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenMap: () -> Unit,
    onAddToFavorites: (String, String?) -> Unit,
    onSaveNote: (String, String) -> Unit
) {
    var noteDialogOpen by remember { mutableStateOf(false) }
    val detailItems = listOf(
        labels.wind to formatWind(city.weather.wind, language),
        labels.humidity to "${city.weather.humidity}%",
        labels.pressure to "${city.weather.pressure} hPa",
        labels.sunrise to city.weather.sunrise,
        labels.sunset to city.weather.sunset
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)
    ) {
        ScreenTopBar(
            title = localizedCity(city.weather, language),
            onBack = onBack
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(Color(0x1A2563EB), MaterialTheme.colorScheme.background)
                        } else {
                            listOf(Color(0xFFF0F7FF), MaterialTheme.colorScheme.background)
                        }
                    )
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIcon(icon = city.weather.icon, modifier = Modifier.size(84.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = signedTemperature(city.weather.temperature, suffix = "°C"),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = localizedCondition(city.weather, language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 20.sp
                )
            }
        }

        Text(
            text = labels.detailedInformation,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        val chunks = detailItems.chunked(2)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            chunks.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { (label, value) ->
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = label,
                            value = value,
                            isDark = isDark
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (!favorite?.note.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            NotePreviewCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                note = favorite.note,
                label = labels.noteLabel,
                isDark = isDark
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = labels.openOnMap,
                icon = Icons.Default.Map,
                rounded = 999.dp,
                onClick = onOpenMap
            )
            OutlineActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = when {
                    favorite == null -> labels.addToFavorites
                    favorite.note.isNullOrBlank() -> labels.addNote
                    else -> labels.editNote
                },
                rounded = 999.dp,
                onClick = { noteDialogOpen = true },
                isDark = isDark
            )
        }
    }

    if (noteDialogOpen) {
        NoteDialog(
            title = when {
                favorite == null -> labels.addToFavorites
                favorite.note.isNullOrBlank() -> labels.addNote
                else -> labels.editNote
            },
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = favorite?.note.orEmpty(),
            confirmText = labels.save,
            dismissText = labels.cancel,
            onDismiss = { noteDialogOpen = false },
            onConfirm = { value ->
                if (favorite == null) {
                    onAddToFavorites(city.id, value)
                } else {
                    onSaveNote(city.id, value)
                }
                noteDialogOpen = false
            }
        )
    }
}

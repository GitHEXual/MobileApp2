package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FavoritesScreen(
    modifier: Modifier,
    favorites: List<FavoriteCity>,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onSelectHome: (String) -> Unit,
    onDeleteFavorite: (String) -> Unit,
    onUpdateNote: (String, String) -> Unit
) {
    var editTarget by remember { mutableStateOf<FavoriteCity?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        ScreenTopBar(
            title = labels.favorites,
            onBack = onBack,
            icon = Icons.Default.Favorite
        )
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppCard(isDark = isDark, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = labels.noFavorites,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                favorites.forEach { favorite ->
                    AppCard(isDark = isDark) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = localizedCity(favorite.weather, language),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onSelectHome(favorite.cityId) },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = labels.setAsHomeCity,
                                            tint = bluePrimary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onOpenDetail(favorite.cityId) },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = labels.details,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = signedTemperature(favorite.weather.temperature, suffix = "°C"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = localizedCondition(favorite.weather, language),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (!favorite.note.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                NotePreviewCard(
                                    note = favorite.note,
                                    label = labels.noteLabel,
                                    isDark = isDark
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniStat(labels.wind, formatWind(favorite.weather.wind, language))
                                MiniStat(labels.humidity, "${favorite.weather.humidity}%")
                                MiniStat(labels.pressure, "${favorite.weather.pressure} hPa")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlineActionButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                text = if (favorite.note.isNullOrBlank()) labels.addNote else labels.editNote,
                                icon = Icons.Default.NoteAlt,
                                onClick = { editTarget = favorite },
                                isDark = isDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onDeleteFavorite(favorite.cityId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = destructive),
                                border = androidx.compose.foundation.BorderStroke(1.dp, destructive.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(labels.delete)
                            }
                        }
                    }
                }
            }
        }
    }

    editTarget?.let { favorite ->
        NoteDialog(
            title = if (favorite.note.isNullOrBlank()) labels.addNote else labels.editNote,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = favorite.note.orEmpty(),
            confirmText = labels.save,
            dismissText = labels.cancel,
            onDismiss = { editTarget = null },
            onConfirm = { value ->
                onUpdateNote(favorite.cityId, value)
                editTarget = null
            }
        )
    }
}

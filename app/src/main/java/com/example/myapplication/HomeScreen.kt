package com.example.myapplication

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    modifier: Modifier,
    city: CityCatalogItem,
    favorite: FavoriteCity?,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onAddToFavorites: (String, String?) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    var noteDialogOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 20.dp)
    ) {
        SearchBar(labels = labels, isDark = isDark)
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            isDark = isDark,
            padding = PaddingValues(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = localizedCity(city.weather, language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${labels.updatedAt} ${city.weather.updatedAt}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                WeatherIcon(icon = city.weather.icon, modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = signedTemperature(city.weather.temperature, suffix = "°"),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = localizedCondition(city.weather, language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlineActionButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = if (favorite != null) labels.inFavorites else labels.addToFavorites,
                        enabled = favorite == null,
                        onClick = { noteDialogOpen = true },
                        isDark = isDark
                    )
                    PrimaryActionButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = labels.details,
                        onClick = { onOpenDetail(city.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = labels.sevenDayForecast,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            city.forecast.forEach { item ->
                ForecastCard(item = item, language = language, isDark = isDark)
            }
        }
    }

    if (noteDialogOpen) {
        NoteDialog(
            title = labels.addNote,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = "",
            confirmText = labels.save,
            dismissText = labels.cancel,
            onDismiss = { noteDialogOpen = false },
            onConfirm = { value ->
                onAddToFavorites(city.id, value)
                noteDialogOpen = false
            }
        )
    }
}

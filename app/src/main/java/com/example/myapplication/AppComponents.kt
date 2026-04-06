package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val lightBorder = Color(0x1A000000)
val darkBorder = Color(0xFF444444)
val bluePrimary = Color(0xFF3B82F6)
val lightMuted = Color(0xFFECECF0)
val darkMuted = Color(0xFF444444)
val noteLight = Color(0xFFFFF8DB)
val noteDark = Color(0xFF3E3414)
val noteBorderLight = Color(0xFFF1D277)
val noteBorderDark = Color(0xFF70631B)
val destructive = Color(0xFFD4183D)

private val actionButtonMinHeight = 52.dp

@Composable
fun SearchBar(labels: AppStrings, isDark: Boolean) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        isDark = isDark,
        backgroundOverride = Color.Transparent,
        padding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(if (isDark) darkMuted else lightMuted)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = labels.searchPlaceholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ForecastCard(item: ForecastDay, language: AppLanguage, isDark: Boolean) {
    AppCard(
        modifier = Modifier.width(74.dp),
        isDark = isDark,
        padding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (language == AppLanguage.RU) item.day else item.dayEn,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            WeatherIcon(icon = item.icon, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = signedTemperature(item.maxTemp, suffix = "°"),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = signedTemperature(item.minTemp, suffix = "°"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isDark: Boolean
) {
    AppCard(modifier = modifier, isDark = isDark) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Text(
                text = value,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NotePreviewCard(
    modifier: Modifier = Modifier,
    note: String,
    label: String,
    isDark: Boolean,
    italic: Boolean = false
) {
    AppCard(
        modifier = modifier,
        isDark = isDark,
        backgroundOverride = if (isDark) noteDark else noteLight,
        borderOverride = if (isDark) noteBorderDark else noteBorderLight
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = note,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
        )
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    icon: ImageVector? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
        }
        content()
    }
}

@Composable
fun SelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val background = when {
        selected -> bluePrimary
        isDark -> darkMuted
        else -> lightMuted
    }
    val foreground = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(text = text, color = foreground)
    }
}

@Composable
fun MiniStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Marker(
    modifier: Modifier,
    title: String,
    primary: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (primary) bluePrimary else if (isDark) darkMuted else lightMuted)
                .border(
                    width = 2.dp,
                    color = if (primary) Color.White else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (primary) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
fun NoteDialog(
    title: String,
    label: String,
    placeholder: String,
    initialValue: String,
    confirmText: String,
    dismissText: String,
    leadingContent: @Composable (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                leadingContent?.invoke()
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(label) },
                    placeholder = { Text(placeholder) },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    backgroundOverride: Color? = null,
    borderOverride: Color? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundOverride ?: MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            borderOverride ?: if (isDark) darkBorder else lightBorder
        )
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
fun PrimaryActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    rounded: Dp = 14.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = actionButtonMinHeight),
        shape = RoundedCornerShape(rounded),
        colors = ButtonDefaults.buttonColors(
            containerColor = bluePrimary,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ActionButtonLabel(
            text = text,
            icon = icon,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OutlineActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    rounded: Dp = 14.dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
    isDark: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = actionButtonMinHeight),
        enabled = enabled,
        shape = RoundedCornerShape(rounded),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) darkBorder else lightBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ActionButtonLabel(
            text = text,
            icon = icon,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ActionButtonLabel(
    text: String,
    icon: ImageVector?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WeatherIcon(icon: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (icon) {
            "sun" -> Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = Color(0xFFF4B400),
                modifier = Modifier.fillMaxSize()
            )

            "cloud" -> Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = Color(0xFF60A5FA),
                modifier = Modifier.fillMaxSize()
            )

            "rain" -> {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier
                        .size(18.dp)
                        .offset(y = 10.dp)
                )
            }

            "cloud-rain" -> {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier
                        .size(18.dp)
                        .offset(x = 10.dp, y = 10.dp)
                )
            }

            "cloud-sun" -> {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = Color(0xFFF4B400),
                    modifier = Modifier
                        .fillMaxSize(0.72f)
                        .offset(x = (-8).dp, y = (-8).dp)
                )
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = Color(0xFF60A5FA),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MapGridOverlay(isDark: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(6) { index ->
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (70 + index * 90).dp),
                color = Color.White.copy(alpha = if (isDark) 0.10f else 0.18f)
            )
        }
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .offset(x = (60 + index * 70).dp)
                    .background(Color.White.copy(alpha = if (isDark) 0.10f else 0.18f))
            )
        }
    }
}

fun localizedCity(weather: WeatherInfo, language: AppLanguage): String {
    return if (language == AppLanguage.RU) weather.city else weather.cityEn
}

fun localizedCondition(weather: WeatherInfo, language: AppLanguage): String {
    return if (language == AppLanguage.RU) weather.condition else weather.conditionEn
}

fun signedTemperature(value: Int, suffix: String): String {
    return "${if (value > 0) "+" else ""}$value$suffix"
}

fun formatWind(wind: Double, language: AppLanguage): String {
    val unit = if (language == AppLanguage.RU) "м/с" else "m/s"
    return "$wind $unit"
}

fun percent(total: Dp, fraction: Float): Dp {
    return total * fraction
}

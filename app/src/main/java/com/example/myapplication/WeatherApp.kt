package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.json.JSONArray
import org.json.JSONObject

private enum class AppLanguage { RU, EN }

private enum class AppThemeMode { LIGHT, DARK }

private enum class AppScreen { HOME, DETAIL, MAP, FAVORITES, SETTINGS }

private data class WeatherInfo(
    val city: String,
    val cityEn: String,
    val temperature: Int,
    val condition: String,
    val conditionEn: String,
    val icon: String,
    val updatedAt: String,
    val wind: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: String,
    val sunset: String
)

private data class ForecastDay(
    val day: String,
    val dayEn: String,
    val icon: String,
    val minTemp: Int,
    val maxTemp: Int
)

private data class FavoriteCity(
    val id: String,
    val weather: WeatherInfo,
    val note: String?
)

private data class AppStrings(
    val searchPlaceholder: String,
    val addToFavorites: String,
    val inFavorites: String,
    val details: String,
    val home: String,
    val map: String,
    val favorites: String,
    val settings: String,
    val updatedAt: String,
    val wind: String,
    val humidity: String,
    val pressure: String,
    val sunrise: String,
    val sunset: String,
    val openOnMap: String,
    val addNote: String,
    val editNote: String,
    val open: String,
    val delete: String,
    val save: String,
    val cancel: String,
    val noteLabel: String,
    val notePlaceholder: String,
    val language: String,
    val russian: String,
    val english: String,
    val theme: String,
    val light: String,
    val dark: String,
    val about: String,
    val weatherData: String,
    val openDetails: String,
    val noFavorites: String,
    val detailedInformation: String,
    val sevenDayForecast: String,
    val version: String,
    val updated: String
)

private val currentWeather = WeatherInfo(
    city = "Москва",
    cityEn = "Moscow",
    temperature = 5,
    condition = "Облачно",
    conditionEn = "Cloudy",
    icon = "cloud",
    updatedAt = "19.02.2026, 10:30",
    wind = 3.4,
    humidity = 82,
    pressure = 1012,
    sunrise = "07:45",
    sunset = "17:32"
)

private val forecast = listOf(
    ForecastDay("Пн", "Mon", "cloud", 3, 7),
    ForecastDay("Вт", "Tue", "cloud-rain", 2, 5),
    ForecastDay("Ср", "Wed", "rain", 1, 4),
    ForecastDay("Чт", "Thu", "cloud-sun", 4, 8),
    ForecastDay("Пт", "Fri", "sun", 5, 10),
    ForecastDay("Сб", "Sat", "sun", 6, 12),
    ForecastDay("Вс", "Sun", "cloud-sun", 5, 9)
)

private val mapCities = listOf(
    WeatherInfo(
        city = "Санкт-Петербург",
        cityEn = "Saint Petersburg",
        temperature = 3,
        condition = "Дождь",
        conditionEn = "Rainy",
        icon = "rain",
        updatedAt = "19.02.2026, 10:30",
        wind = 5.2,
        humidity = 88,
        pressure = 1008,
        sunrise = "08:15",
        sunset = "17:10"
    ),
    WeatherInfo(
        city = "Казань",
        cityEn = "Kazan",
        temperature = -2,
        condition = "Облачно",
        conditionEn = "Cloudy",
        icon = "cloud",
        updatedAt = "19.02.2026, 10:30",
        wind = 2.8,
        humidity = 75,
        pressure = 1015,
        sunrise = "07:50",
        sunset = "17:25"
    ),
    WeatherInfo(
        city = "Сочи",
        cityEn = "Sochi",
        temperature = 12,
        condition = "Солнечно",
        conditionEn = "Sunny",
        icon = "sun",
        updatedAt = "19.02.2026, 10:30",
        wind = 1.5,
        humidity = 65,
        pressure = 1018,
        sunrise = "07:20",
        sunset = "18:05"
    )
)

private val stringsRu = AppStrings(
    searchPlaceholder = "Поиск города",
    addToFavorites = "Добавить в избранное",
    inFavorites = "В избранном",
    details = "Подробнее",
    home = "Домой",
    map = "Карта",
    favorites = "Избранное",
    settings = "Настройки",
    updatedAt = "Обновлено",
    wind = "Ветер",
    humidity = "Влажность",
    pressure = "Давление",
    sunrise = "Восход",
    sunset = "Закат",
    openOnMap = "Открыть на карте",
    addNote = "Добавить заметку",
    editNote = "Редактировать заметку",
    open = "Открыть",
    delete = "Удалить",
    save = "Сохранить",
    cancel = "Отмена",
    noteLabel = "Заметка",
    notePlaceholder = "Введите заметку...",
    language = "Язык",
    russian = "Русский",
    english = "Английский",
    theme = "Тема",
    light = "Светлая",
    dark = "Тёмная",
    about = "О приложении",
    weatherData = "Данные погоды — OpenWeather API",
    openDetails = "Перейти в детали",
    noFavorites = "Нет избранных городов",
    detailedInformation = "Подробная информация",
    sevenDayForecast = "7-дневный прогноз",
    version = "Версия: 1.0.0",
    updated = "Обновлено: 19.02.2026"
)

private val stringsEn = AppStrings(
    searchPlaceholder = "Search city",
    addToFavorites = "Add to favorites",
    inFavorites = "In favorites",
    details = "Details",
    home = "Home",
    map = "Map",
    favorites = "Favorites",
    settings = "Settings",
    updatedAt = "Updated at",
    wind = "Wind",
    humidity = "Humidity",
    pressure = "Pressure",
    sunrise = "Sunrise",
    sunset = "Sunset",
    openOnMap = "Open on map",
    addNote = "Add note",
    editNote = "Edit note",
    open = "Open",
    delete = "Delete",
    save = "Save",
    cancel = "Cancel",
    noteLabel = "Note",
    notePlaceholder = "Enter note...",
    language = "Language",
    russian = "Russian",
    english = "English",
    theme = "Theme",
    light = "Light",
    dark = "Dark",
    about = "About",
    weatherData = "Weather data — OpenWeather API",
    openDetails = "Open details",
    noFavorites = "No favorite cities",
    detailedInformation = "Detailed Information",
    sevenDayForecast = "7-day forecast",
    version = "Version: 1.0.0",
    updated = "Updated: 19.02.2026"
)

private fun strings(language: AppLanguage): AppStrings {
    return if (language == AppLanguage.RU) stringsRu else stringsEn
}

private class WeatherStorage(context: Context) {
    private val preferences = context.getSharedPreferences("weather_app_prefs", Context.MODE_PRIVATE)

    fun loadLanguage(): AppLanguage {
        return runCatching {
            AppLanguage.valueOf(preferences.getString("language", AppLanguage.RU.name) ?: AppLanguage.RU.name)
        }.getOrDefault(AppLanguage.RU)
    }

    fun saveLanguage(value: AppLanguage) {
        preferences.edit().putString("language", value.name).apply()
    }

    fun loadTheme(): AppThemeMode {
        return runCatching {
            AppThemeMode.valueOf(preferences.getString("theme", AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name)
        }.getOrDefault(AppThemeMode.LIGHT)
    }

    fun saveTheme(value: AppThemeMode) {
        preferences.edit().putString("theme", value.name).apply()
    }

    fun loadFavorites(): List<FavoriteCity> {
        val raw = preferences.getString("favorites", null) ?: return emptyList()
        return runCatching {
            val json = JSONArray(raw)
            buildList {
                for (index in 0 until json.length()) {
                    val item = json.getJSONObject(index)
                    val weatherJson = item.getJSONObject("weather")
                    add(
                        FavoriteCity(
                            id = item.getString("id"),
                            weather = weatherJson.toWeatherInfo(),
                            note = item.optString("note").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveFavorites(value: List<FavoriteCity>) {
        val array = JSONArray()
        value.forEach { favorite ->
            array.put(
                JSONObject().apply {
                    put("id", favorite.id)
                    put("note", favorite.note.orEmpty())
                    put("weather", favorite.weather.toJson())
                }
            )
        }
        preferences.edit().putString("favorites", array.toString()).apply()
    }
}

private fun WeatherInfo.toJson(): JSONObject {
    return JSONObject().apply {
        put("city", city)
        put("cityEn", cityEn)
        put("temperature", temperature)
        put("condition", condition)
        put("conditionEn", conditionEn)
        put("icon", icon)
        put("updatedAt", updatedAt)
        put("wind", wind)
        put("humidity", humidity)
        put("pressure", pressure)
        put("sunrise", sunrise)
        put("sunset", sunset)
    }
}

private fun JSONObject.toWeatherInfo(): WeatherInfo {
    return WeatherInfo(
        city = getString("city"),
        cityEn = getString("cityEn"),
        temperature = getInt("temperature"),
        condition = getString("condition"),
        conditionEn = getString("conditionEn"),
        icon = getString("icon"),
        updatedAt = getString("updatedAt"),
        wind = getDouble("wind"),
        humidity = getInt("humidity"),
        pressure = getInt("pressure"),
        sunrise = getString("sunrise"),
        sunset = getString("sunset")
    )
}

private val lightBorder = Color(0x1A000000)
private val darkBorder = Color(0xFF444444)
private val bluePrimary = Color(0xFF3B82F6)
private val blueSecondary = Color(0xFF2563EB)
private val lightMuted = Color(0xFFECECF0)
private val darkMuted = Color(0xFF444444)
private val noteLight = Color(0xFFFFF8DB)
private val noteDark = Color(0xFF3E3414)
private val noteBorderLight = Color(0xFFF1D277)
private val noteBorderDark = Color(0xFF70631B)
private val destructive = Color(0xFFD4183D)

@Composable
fun WeatherApp() {
    val context = LocalContext.current
    val storage = remember(context) { WeatherStorage(context.applicationContext) }

    var language by remember { mutableStateOf(storage.loadLanguage()) }
    var theme by remember { mutableStateOf(storage.loadTheme()) }
    var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var favorites by remember { mutableStateOf(storage.loadFavorites()) }
    var selectedFavoriteId by rememberSaveable { mutableStateOf<String?>(null) }

    val isDark = theme == AppThemeMode.DARK
    val labels = strings(language)
    val selectedFavorite = favorites.firstOrNull { it.id == selectedFavoriteId }
    val homeFavorite = favorites.firstOrNull { it.weather.city == currentWeather.city }

    fun updateFavorites(newFavorites: List<FavoriteCity>) {
        favorites = newFavorites
        storage.saveFavorites(newFavorites)
    }

    fun addFavorite(weather: WeatherInfo, note: String?) {
        if (favorites.any { it.weather.city == weather.city }) return
        updateFavorites(
            favorites + FavoriteCity(
                id = "${weather.city}-${System.currentTimeMillis()}",
                weather = weather,
                note = note?.trim()?.takeIf { it.isNotEmpty() }
            )
        )
    }

    fun updateFavoriteNote(id: String, note: String) {
        updateFavorites(
            favorites.map { favorite ->
                if (favorite.id == id) {
                    favorite.copy(note = note.trim().takeIf { it.isNotEmpty() })
                } else {
                    favorite
                }
            }
        )
    }

    fun deleteFavorite(id: String) {
        updateFavorites(favorites.filterNot { it.id == id })
        if (selectedFavoriteId == id) {
            selectedFavoriteId = null
            screen = AppScreen.FAVORITES
        }
    }

    fun openDetail(id: String? = null) {
        selectedFavoriteId = id
        screen = AppScreen.DETAIL
    }

    fun goHome() {
        selectedFavoriteId = null
        screen = AppScreen.HOME
    }

    MyApplicationTheme(darkTheme = isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (screen != AppScreen.DETAIL) {
                        BottomNavigationBar(
                            screen = screen,
                            labels = labels,
                            onNavigate = { screen = it }
                        )
                    }
                }
            ) { padding ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(
                        modifier = Modifier.padding(padding),
                        weather = currentWeather,
                        forecast = forecast,
                        labels = labels,
                        language = language,
                        isDark = isDark,
                        isInFavorites = homeFavorite != null,
                        onAddToFavorites = { note -> addFavorite(currentWeather, note) },
                        onOpenDetail = { openDetail() }
                    )

                    AppScreen.DETAIL -> DetailScreen(
                        modifier = Modifier.padding(padding),
                        weather = selectedFavorite?.weather ?: currentWeather,
                        note = selectedFavorite?.note,
                        labels = labels,
                        language = language,
                        isDark = isDark,
                        onBack = ::goHome,
                        onOpenMap = { screen = AppScreen.MAP },
                        onSaveNote = { note ->
                            selectedFavorite?.let { updateFavoriteNote(it.id, note) }
                        }
                    )

                    AppScreen.MAP -> MapScreen(
                        modifier = Modifier.padding(padding),
                        favorites = favorites,
                        labels = labels,
                        language = language,
                        isDark = isDark,
                        onBack = ::goHome,
                        onOpenDetail = { id -> openDetail(id) },
                        onDeleteFavorite = ::deleteFavorite,
                        onAddToFavorites = ::addFavorite
                    )

                    AppScreen.FAVORITES -> FavoritesScreen(
                        modifier = Modifier.padding(padding),
                        favorites = favorites,
                        labels = labels,
                        language = language,
                        isDark = isDark,
                        onBack = ::goHome,
                        onOpenDetail = { id -> openDetail(id) },
                        onDeleteFavorite = ::deleteFavorite,
                        onUpdateNote = ::updateFavoriteNote
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        modifier = Modifier.padding(padding),
                        labels = labels,
                        language = language,
                        theme = theme,
                        isDark = isDark,
                        onBack = ::goHome,
                        onLanguageChange = {
                            language = it
                            storage.saveLanguage(it)
                        },
                        onThemeChange = {
                            theme = it
                            storage.saveTheme(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    weather: WeatherInfo,
    forecast: List<ForecastDay>,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    isInFavorites: Boolean,
    onAddToFavorites: (String?) -> Unit,
    onOpenDetail: () -> Unit
) {
    var noteDialogOpen by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
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
                    text = localizedCity(weather, language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${labels.updatedAt} ${weather.updatedAt}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                WeatherIcon(icon = weather.icon, modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = signedTemperature(weather.temperature, suffix = "°"),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = localizedCondition(weather, language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineActionButton(
                        modifier = Modifier.weight(1f),
                        text = if (isInFavorites) labels.inFavorites else labels.addToFavorites,
                        enabled = !isInFavorites,
                        onClick = { noteDialogOpen = true },
                        isDark = isDark
                    )
                    PrimaryActionButton(
                        modifier = Modifier.weight(1f),
                        text = labels.details,
                        onClick = onOpenDetail
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
            forecast.forEach { item ->
                ForecastCard(item = item, language = language, isDark = isDark)
            }
        }
    }

    if (noteDialogOpen) {
        NoteDialog(
            title = labels.addNote,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = noteText,
            confirmText = labels.save,
            dismissText = labels.cancel,
            onDismiss = {
                noteDialogOpen = false
                noteText = ""
            },
            onConfirm = { value ->
                onAddToFavorites(value)
                noteDialogOpen = false
                noteText = ""
            }
        )
    }
}

@Composable
private fun DetailScreen(
    modifier: Modifier,
    weather: WeatherInfo,
    note: String?,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenMap: () -> Unit,
    onSaveNote: (String) -> Unit
) {
    var noteDialogOpen by remember { mutableStateOf(false) }
    var noteText by remember(note) { mutableStateOf(note.orEmpty()) }
    val detailItems = listOf(
        labels.wind to formatWind(weather.wind, language),
        labels.humidity to "${weather.humidity}%",
        labels.pressure to "${weather.pressure} hPa",
        labels.sunrise to weather.sunrise,
        labels.sunset to weather.sunset
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)
    ) {
        ScreenTopBar(
            title = localizedCity(weather, language),
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
                WeatherIcon(icon = weather.icon, modifier = Modifier.size(84.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = signedTemperature(weather.temperature, suffix = "°C"),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = localizedCondition(weather, language),
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

        if (!note.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            NotePreviewCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                note = note,
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
                text = if (note.isNullOrBlank()) labels.addNote else labels.editNote,
                rounded = 999.dp,
                onClick = { noteDialogOpen = true },
                isDark = isDark
            )
        }
    }

    if (noteDialogOpen) {
        NoteDialog(
            title = if (note.isNullOrBlank()) labels.addNote else labels.editNote,
            label = labels.noteLabel,
            placeholder = labels.notePlaceholder,
            initialValue = noteText,
            confirmText = labels.save,
            dismissText = labels.cancel,
            onDismiss = { noteDialogOpen = false },
            onConfirm = { value ->
                onSaveNote(value)
                noteDialogOpen = false
            }
        )
    }
}

@Composable
private fun MapScreen(
    modifier: Modifier,
    favorites: List<FavoriteCity>,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onDeleteFavorite: (String) -> Unit,
    onAddToFavorites: (WeatherInfo, String?) -> Unit
) {
    var selectedFavorite by remember { mutableStateOf<FavoriteCity?>(null) }
    var selectedAvailable by remember { mutableStateOf<WeatherInfo?>(null) }
    val availableCities = remember(favorites) {
        (listOf(currentWeather) + mapCities).filter { weather ->
            favorites.none { it.weather.city == weather.city }
        }
    }

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
            favorites.forEachIndexed { index, favorite ->
                Marker(
                    modifier = Modifier.offset(
                        x = percent(maxWidth, if (index % 2 == 0) 0.20f else 0.60f) - 24.dp,
                        y = percent(maxHeight, 0.30f + (index % 3) * 0.20f) - 36.dp
                    ),
                    title = localizedCity(favorite.weather, language),
                    primary = true,
                    isDark = isDark,
                    onClick = { selectedFavorite = favorite }
                )
            }
            availableCities.forEachIndexed { index, city ->
                Marker(
                    modifier = Modifier.offset(
                        x = percent(maxWidth, 0.30f + (index % 3) * 0.25f) - 24.dp,
                        y = percent(maxHeight, 0.40f + (index % 4) * 0.15f) - 36.dp
                    ),
                    title = localizedCity(city, language),
                    primary = false,
                    isDark = isDark,
                    onClick = { selectedAvailable = city }
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
                        onOpenDetail(favorite.id)
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
                        onDeleteFavorite(favorite.id)
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
                            Text(localizedCity(city, language), fontWeight = FontWeight.Medium)
                            Text(
                                localizedCondition(city, language),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            signedTemperature(city.temperature, suffix = "°"),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            },
            onDismiss = { selectedAvailable = null },
            onConfirm = { value ->
                onAddToFavorites(city, value)
                selectedAvailable = null
            }
        )
    }
}

@Composable
private fun FavoritesScreen(
    modifier: Modifier,
    favorites: List<FavoriteCity>,
    labels: AppStrings,
    language: AppLanguage,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
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
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                favorites.forEach { favorite ->
                    AppCard(isDark = isDark) {
                        Column {
                            Text(
                                text = localizedCity(favorite.weather, language),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = signedTemperature(favorite.weather.temperature, suffix = "°C"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = localizedCondition(favorite.weather, language),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PrimaryActionButton(
                                    modifier = Modifier.weight(1f),
                                    text = labels.open,
                                    onClick = { onOpenDetail(favorite.id) }
                                )
                                OutlineActionButton(
                                    modifier = Modifier.weight(1f),
                                    text = if (favorite.note.isNullOrBlank()) labels.addNote else labels.editNote,
                                    icon = Icons.Default.NoteAlt,
                                    onClick = { editTarget = favorite },
                                    isDark = isDark
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onDeleteFavorite(favorite.id) },
                                modifier = Modifier.fillMaxWidth(),
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
                onUpdateNote(favorite.id, value)
                editTarget = null
            }
        )
    }
}

@Composable
private fun SettingsScreen(
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${labels.updated}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(labels: AppStrings, isDark: Boolean) {
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
private fun ForecastCard(item: ForecastDay, language: AppLanguage, isDark: Boolean) {
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
private fun StatCard(
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
private fun NotePreviewCard(
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
private fun ScreenTopBar(
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
                    tint = if (icon == Icons.Default.Favorite) Color(0xFFF4B400) else MaterialTheme.colorScheme.onSurface,
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
private fun BottomNavigationBar(
    screen: AppScreen,
    labels: AppStrings,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        val items = listOf(
            Triple(AppScreen.HOME, Icons.Default.Home, labels.home),
            Triple(AppScreen.MAP, Icons.Default.Map, labels.map),
            Triple(AppScreen.FAVORITES, Icons.Default.Favorite, labels.favorites),
            Triple(AppScreen.SETTINGS, Icons.Default.Settings, labels.settings)
        )
        items.forEach { (target, icon, label) ->
            NavigationBarItem(
                selected = screen == target,
                onClick = { onNavigate(target) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun SettingsSection(
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
private fun SelectionButton(
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
private fun MiniStat(label: String, value: String) {
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
private fun Marker(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDialog(
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
private fun AppCard(
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
private fun PrimaryActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    rounded: Dp = 14.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.wrapContentHeight(),
        shape = RoundedCornerShape(rounded),
        colors = ButtonDefaults.buttonColors(
            containerColor = bluePrimary,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
private fun OutlineActionButton(
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
        modifier = modifier.wrapContentHeight(),
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
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
private fun WeatherIcon(icon: String, modifier: Modifier = Modifier) {
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
private fun MapGridOverlay(isDark: Boolean) {
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

private fun localizedCity(weather: WeatherInfo, language: AppLanguage): String {
    return if (language == AppLanguage.RU) weather.city else weather.cityEn
}

private fun localizedCondition(weather: WeatherInfo, language: AppLanguage): String {
    return if (language == AppLanguage.RU) weather.condition else weather.conditionEn
}

private fun signedTemperature(value: Int, suffix: String): String {
    return "${if (value > 0) "+" else ""}$value$suffix"
}

private fun formatWind(wind: Double, language: AppLanguage): String {
    val unit = if (language == AppLanguage.RU) "м/с" else "m/s"
    return "$wind $unit"
}

private fun percent(total: Dp, fraction: Float): Dp {
    return total * fraction
}

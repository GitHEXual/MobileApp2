package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherAppViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = AppPreferences(application)
    private val repository = FavoritesRepository(AppDatabase.getInstance(application).favoriteCityDao())

    private val languageFlow = MutableStateFlow(preferences.loadLanguage())
    private val themeFlow = MutableStateFlow(preferences.loadTheme())

    val uiState: StateFlow<WeatherAppUiState> = combine(
        languageFlow,
        themeFlow,
        repository.favorites
    ) { language, theme, favorites ->
        WeatherAppUiState(
            language = language,
            theme = theme,
            labels = strings(language),
            homeCity = WeatherCatalog.homeCity,
            cities = WeatherCatalog.cities,
            favorites = favorites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeatherAppUiState(
            language = preferences.loadLanguage(),
            theme = preferences.loadTheme()
        )
    )

    fun setLanguage(language: AppLanguage) {
        languageFlow.value = language
        preferences.saveLanguage(language)
    }

    fun setTheme(theme: AppThemeMode) {
        themeFlow.value = theme
        preferences.saveTheme(theme)
    }

    fun addFavorite(cityId: String, note: String?) {
        viewModelScope.launch {
            repository.addFavorite(cityId, note)
        }
    }

    fun updateFavoriteNote(cityId: String, note: String) {
        viewModelScope.launch {
            repository.updateFavoriteNote(cityId, note)
        }
    }

    fun deleteFavorite(cityId: String) {
        viewModelScope.launch {
            repository.deleteFavorite(cityId)
        }
    }
}

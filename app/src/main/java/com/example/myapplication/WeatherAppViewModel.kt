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
    private val remoteRepository = WeatherRemoteRepository()

    private val languageFlow = MutableStateFlow(preferences.loadLanguage())
    private val themeFlow = MutableStateFlow(preferences.loadTheme())
    private val citiesFlow = MutableStateFlow(WeatherCatalog.offlineCities())
    private val loadingFlow = MutableStateFlow(true)
    private val networkErrorFlow = MutableStateFlow<String?>(null)

    private data class LangThemeFavorites(
        val language: AppLanguage,
        val theme: AppThemeMode,
        val entities: List<FavoriteCityEntity>
    )

    private data class CitiesLoadingError(
        val cities: List<CityCatalogItem>,
        val loading: Boolean,
        val err: String?
    )

    val uiState: StateFlow<WeatherAppUiState> = combine(
        combine(languageFlow, themeFlow, repository.favoriteEntities) { language, theme, entities ->
            LangThemeFavorites(language, theme, entities)
        },
        combine(citiesFlow, loadingFlow, networkErrorFlow) { cities, loading, err ->
            CitiesLoadingError(cities, loading, err)
        }
    ) { ltf, cle ->
        WeatherAppUiState(
            language = ltf.language,
            theme = ltf.theme,
            labels = strings(ltf.language),
            homeCity = cle.cities.firstOrNull { it.id == WeatherCatalog.HOME_CITY_ID } ?: WeatherCatalog.homeCity,
            cities = cle.cities,
            favorites = ltf.entities.mapNotNull { it.toFavoriteCity(cle.cities) },
            isLoading = cle.loading,
            networkErrorMessage = cle.err
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeatherAppUiState(
            language = preferences.loadLanguage(),
            theme = preferences.loadTheme(),
            isLoading = true
        )
    )

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        viewModelScope.launch {
            loadingFlow.value = true
            networkErrorFlow.value = null
            val result = remoteRepository.loadAllCities()
            citiesFlow.value = result.cities
            if (!result.usedNetwork) {
                networkErrorFlow.value = strings(languageFlow.value).offlineWeatherHint
            }
            loadingFlow.value = false
        }
    }

    fun consumeNetworkError() {
        networkErrorFlow.value = null
    }

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

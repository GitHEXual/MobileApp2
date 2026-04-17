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
    private val homeSelectionFlow = MutableStateFlow(preferences.loadHomeCitySelection())
    private val mapKeysRevision = MutableStateFlow(0)

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
        },
        homeSelectionFlow,
        mapKeysRevision
    ) { ltf, cle, homeSel, _ ->
        val labels = strings(ltf.language)
        val savedHomeId = when (homeSel) {
            is HomeCitySelection.Catalog -> homeSel.cityId
            is HomeCitySelection.Custom -> homeSel.id
        }
        val homeCity = cle.cities.firstOrNull { it.id == savedHomeId } ?: WeatherCatalog.homeCity
        WeatherAppUiState(
            language = ltf.language,
            theme = ltf.theme,
            labels = labels,
            homeCity = homeCity,
            homeCityId = savedHomeId,
            cities = cle.cities,
            favorites = ltf.entities.mapNotNull { it.toFavoriteCity(cle.cities) },
            isLoading = cle.loading,
            networkErrorMessage = cle.err,
            mapKeyRows = buildMapKeyRows(labels)
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

    private fun buildMapKeyRows(labels: AppStrings): List<MapKeyRow> {
        val active = preferences.loadActiveMapKeyId()
        val rows = mutableListOf(
            MapKeyRow(
                id = MapKitConfig.BUILTIN_KEY_ID,
                displayName = labels.mapKeysBuiltinName,
                maskedKey = maskMapApiKey(MapKitConfig.DEFAULT_API_KEY),
                isBuiltin = true,
                isActive = active == MapKitConfig.BUILTIN_KEY_ID
            )
        )
        preferences.loadMapApiKeys().forEach { k ->
            rows += MapKeyRow(
                id = k.id,
                displayName = k.displayName,
                maskedKey = maskMapApiKey(k.apiKey),
                isBuiltin = false,
                isActive = active == k.id
            )
        }
        return rows
    }

    fun refreshWeather() {
        viewModelScope.launch {
            loadingFlow.value = true
            networkErrorFlow.value = null
            val result = remoteRepository.loadAllCities()
            var cities = result.cities
            val homeSel = preferences.loadHomeCitySelection()
            if (homeSel is HomeCitySelection.Custom) {
                val place = homeSel.toCityPlace()
                val custom = remoteRepository.loadForecastForPlace(place)
                if (custom != null) {
                    cities = cities.filter { it.id != custom.id } + custom
                }
            }
            citiesFlow.value = cities
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

    fun setHomeCityCatalog(cityId: String) {
        val sel = HomeCitySelection.Catalog(cityId)
        preferences.saveHomeCitySelection(sel)
        homeSelectionFlow.value = sel
    }

    fun setHomeCityCustom(latitude: Double, longitude: Double, nameRu: String, nameEn: String) {
        val id = HomeCityIds.customId(latitude, longitude)
        val sel = HomeCitySelection.Custom(id, latitude, longitude, nameRu, nameEn)
        preferences.saveHomeCitySelection(sel)
        homeSelectionFlow.value = sel
        refreshWeather()
    }

    fun addMapApiKey(displayName: String, apiKey: String) {
        preferences.addMapApiKey(displayName, apiKey)
        mapKeysRevision.value++
    }

    fun removeMapApiKey(id: String) {
        preferences.removeMapApiKey(id)
        mapKeysRevision.value++
    }

    fun setActiveMapApiKey(id: String) {
        if (preferences.loadActiveMapKeyId() == id) return
        preferences.setActiveMapKeyId(id)
        mapKeysRevision.value++
        AppRestarter.restartProcess(getApplication())
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

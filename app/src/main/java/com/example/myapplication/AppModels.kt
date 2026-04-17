package com.example.myapplication

enum class AppLanguage { RU, EN }

enum class AppThemeMode { LIGHT, DARK }

data class WeatherInfo(
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

data class ForecastDay(
    val day: String,
    val dayEn: String,
    val icon: String,
    val minTemp: Int,
    val maxTemp: Int
)

data class MapPosition(
    val xFraction: Float,
    val yFraction: Float
)

data class CityCatalogItem(
    val id: String,
    val weather: WeatherInfo,
    val mapPosition: MapPosition,
    val forecast: List<ForecastDay> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class FavoriteCity(
    val cityId: String,
    val weather: WeatherInfo,
    val note: String?,
    val createdAt: Long,
    val mapPosition: MapPosition
)

data class WeatherAppUiState(
    val language: AppLanguage = AppLanguage.RU,
    val theme: AppThemeMode = AppThemeMode.LIGHT,
    val labels: AppStrings = stringsRu,
    val homeCity: CityCatalogItem = WeatherCatalog.homeCity,
    val homeCityId: String = WeatherCatalog.HOME_CITY_ID,
    val cities: List<CityCatalogItem> = WeatherCatalog.offlineCities(),
    val favorites: List<FavoriteCity> = emptyList(),
    val isLoading: Boolean = false,
    val networkErrorMessage: String? = null
) {
    val isDark: Boolean
        get() = theme == AppThemeMode.DARK
}

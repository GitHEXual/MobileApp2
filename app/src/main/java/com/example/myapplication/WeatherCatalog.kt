package com.example.myapplication

data class CityPlace(
    val id: String,
    val city: String,
    val cityEn: String,
    val latitude: Double,
    val longitude: Double,
    val mapPosition: MapPosition
)

object WeatherCatalog {
    const val HOME_CITY_ID = "moscow"

    val places: List<CityPlace> = listOf(
        CityPlace(
            id = HOME_CITY_ID,
            city = "Москва",
            cityEn = "Moscow",
            latitude = 55.7558,
            longitude = 37.6173,
            mapPosition = MapPosition(0.24f, 0.28f)
        ),
        CityPlace(
            id = "saint-petersburg",
            city = "Санкт-Петербург",
            cityEn = "Saint Petersburg",
            latitude = 59.9343,
            longitude = 30.3351,
            mapPosition = MapPosition(0.18f, 0.14f)
        ),
        CityPlace(
            id = "kazan",
            city = "Казань",
            cityEn = "Kazan",
            latitude = 55.7887,
            longitude = 49.1221,
            mapPosition = MapPosition(0.47f, 0.34f)
        ),
        CityPlace(
            id = "sochi",
            city = "Сочи",
            cityEn = "Sochi",
            latitude = 43.6028,
            longitude = 39.7342,
            mapPosition = MapPosition(0.30f, 0.70f)
        )
    )

    private val homeForecastOffline = listOf(
        ForecastDay("Пн", "Mon", "cloud", 3, 7),
        ForecastDay("Вт", "Tue", "cloud-rain", 2, 5),
        ForecastDay("Ср", "Wed", "rain", 1, 4),
        ForecastDay("Чт", "Thu", "cloud-sun", 4, 8),
        ForecastDay("Пт", "Fri", "sun", 5, 10),
        ForecastDay("Сб", "Sat", "sun", 6, 12),
        ForecastDay("Вс", "Sun", "cloud-sun", 5, 9)
    )

    fun offlineCities(): List<CityCatalogItem> = listOf(
        CityCatalogItem(
            id = HOME_CITY_ID,
            weather = WeatherInfo(
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
            ),
            mapPosition = places.first { it.id == HOME_CITY_ID }.mapPosition,
            forecast = homeForecastOffline,
            latitude = places.first { it.id == HOME_CITY_ID }.latitude,
            longitude = places.first { it.id == HOME_CITY_ID }.longitude
        ),
        CityCatalogItem(
            id = "saint-petersburg",
            weather = WeatherInfo(
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
            mapPosition = places.first { it.id == "saint-petersburg" }.mapPosition,
            latitude = places.first { it.id == "saint-petersburg" }.latitude,
            longitude = places.first { it.id == "saint-petersburg" }.longitude
        ),
        CityCatalogItem(
            id = "kazan",
            weather = WeatherInfo(
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
            mapPosition = places.first { it.id == "kazan" }.mapPosition,
            latitude = places.first { it.id == "kazan" }.latitude,
            longitude = places.first { it.id == "kazan" }.longitude
        ),
        CityCatalogItem(
            id = "sochi",
            weather = WeatherInfo(
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
            ),
            mapPosition = places.first { it.id == "sochi" }.mapPosition,
            latitude = places.first { it.id == "sochi" }.latitude,
            longitude = places.first { it.id == "sochi" }.longitude
        )
    )

    val homeCity: CityCatalogItem
        get() = offlineCities().first { it.id == HOME_CITY_ID }

    val cities: List<CityCatalogItem>
        get() = offlineCities()

    fun findCity(cityId: String): CityCatalogItem? {
        return offlineCities().firstOrNull { it.id == cityId }
    }

    fun findPlace(cityId: String): CityPlace? = places.firstOrNull { it.id == cityId }
}

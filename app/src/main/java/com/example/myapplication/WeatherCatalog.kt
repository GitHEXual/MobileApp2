package com.example.myapplication

object WeatherCatalog {
    const val HOME_CITY_ID = "moscow"

    private val homeForecast = listOf(
        ForecastDay("Пн", "Mon", "cloud", 3, 7),
        ForecastDay("Вт", "Tue", "cloud-rain", 2, 5),
        ForecastDay("Ср", "Wed", "rain", 1, 4),
        ForecastDay("Чт", "Thu", "cloud-sun", 4, 8),
        ForecastDay("Пт", "Fri", "sun", 5, 10),
        ForecastDay("Сб", "Sat", "sun", 6, 12),
        ForecastDay("Вс", "Sun", "cloud-sun", 5, 9)
    )

    val cities = listOf(
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
            mapPosition = MapPosition(0.24f, 0.28f),
            forecast = homeForecast
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
            mapPosition = MapPosition(0.18f, 0.14f)
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
            mapPosition = MapPosition(0.47f, 0.34f)
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
            mapPosition = MapPosition(0.30f, 0.70f)
        )
    )

    val homeCity: CityCatalogItem
        get() = cities.first { it.id == HOME_CITY_ID }

    fun findCity(cityId: String): CityCatalogItem? {
        return cities.firstOrNull { it.id == cityId }
    }
}

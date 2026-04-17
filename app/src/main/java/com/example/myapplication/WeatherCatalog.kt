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

    /** Для городов без отдельной позиции на старом «плоском» виджете карты в каталоге. */
    private val mapPosDefault = MapPosition(0.5f, 0.5f)

    /**
     * Все города каталога погоды (координаты для карты и запросов Open-Meteo).
     * Раньше было 4 точки — на карте почти нечего показывать; держим десятки крупных городов РФ + несколько столиц.
     */
    val places: List<CityPlace> = listOf(
        CityPlace(HOME_CITY_ID, "Москва", "Moscow", 55.7558, 37.6173, MapPosition(0.24f, 0.28f)),
        CityPlace("saint-petersburg", "Санкт-Петербург", "Saint Petersburg", 59.9343, 30.3351, MapPosition(0.18f, 0.14f)),
        CityPlace("kazan", "Казань", "Kazan", 55.7887, 49.1221, MapPosition(0.47f, 0.34f)),
        CityPlace("sochi", "Сочи", "Sochi", 43.6028, 39.7342, MapPosition(0.30f, 0.70f)),
        CityPlace("novosibirsk", "Новосибирск", "Novosibirsk", 55.0084, 82.9357, mapPosDefault),
        CityPlace("yekaterinburg", "Екатеринбург", "Yekaterinburg", 56.8389, 60.6057, mapPosDefault),
        CityPlace("nizhny-novgorod", "Нижний Новгород", "Nizhny Novgorod", 56.2965, 43.9361, mapPosDefault),
        CityPlace("samara", "Самара", "Samara", 53.2001, 50.1500, mapPosDefault),
        CityPlace("omsk", "Омск", "Omsk", 54.9885, 73.3242, mapPosDefault),
        CityPlace("chelyabinsk", "Челябинск", "Chelyabinsk", 55.1644, 61.4368, mapPosDefault),
        CityPlace("rostov-on-don", "Ростов-на-Дону", "Rostov-on-Don", 47.2357, 39.7015, mapPosDefault),
        CityPlace("ufa", "Уфа", "Ufa", 54.7388, 55.9721, mapPosDefault),
        CityPlace("krasnoyarsk", "Красноярск", "Krasnoyarsk", 56.0184, 92.8672, mapPosDefault),
        CityPlace("voronezh", "Воронеж", "Voronezh", 51.6720, 39.1843, mapPosDefault),
        CityPlace("perm", "Пермь", "Perm", 58.0105, 56.2502, mapPosDefault),
        CityPlace("volgograd", "Волгоград", "Volgograd", 48.7080, 44.5133, mapPosDefault),
        CityPlace("krasnodar", "Краснодар", "Krasnodar", 45.0355, 38.9753, mapPosDefault),
        CityPlace("saratov", "Саратов", "Saratov", 51.5336, 46.0342, mapPosDefault),
        CityPlace("tyumen", "Тюмень", "Tyumen", 57.1522, 65.5272, mapPosDefault),
        CityPlace("tolyatti", "Тольятти", "Tolyatti", 53.5303, 49.3461, mapPosDefault),
        CityPlace("izhevsk", "Ижевск", "Izhevsk", 56.8528, 53.2114, mapPosDefault),
        CityPlace("barnaul", "Барнаул", "Barnaul", 53.3606, 83.7636, mapPosDefault),
        CityPlace("ulyanovsk", "Ульяновск", "Ulyanovsk", 54.3142, 48.4031, mapPosDefault),
        CityPlace("irkutsk", "Иркутск", "Irkutsk", 52.2864, 104.2807, mapPosDefault),
        CityPlace("khabarovsk", "Хабаровск", "Khabarovsk", 48.4827, 135.0840, mapPosDefault),
        CityPlace("yaroslavl", "Ярославль", "Yaroslavl", 57.6261, 39.8845, mapPosDefault),
        CityPlace("vladivostok", "Владивосток", "Vladivostok", 43.1056, 131.8740, mapPosDefault),
        CityPlace("makhachkala", "Махачкала", "Makhachkala", 42.9849, 47.5047, mapPosDefault),
        CityPlace("tomsk", "Томск", "Tomsk", 56.4884, 84.9480, mapPosDefault),
        CityPlace("orenburg", "Оренбург", "Orenburg", 51.7727, 55.0988, mapPosDefault),
        CityPlace("kemerovo", "Кемерово", "Kemerovo", 55.3904, 86.0468, mapPosDefault),
        CityPlace("novokuznetsk", "Новокузнецк", "Novokuznetsk", 53.7596, 87.1216, mapPosDefault),
        CityPlace("ryazan", "Рязань", "Ryazan", 54.6292, 39.7365, mapPosDefault),
        CityPlace("astrakhan", "Астрахань", "Astrakhan", 46.3497, 48.0408, mapPosDefault),
        CityPlace("naberezhny-chelny", "Набережные Челны", "Naberezhny Chelny", 55.7251, 52.4052, mapPosDefault),
        CityPlace("penza", "Пенза", "Penza", 53.2007, 45.0046, mapPosDefault),
        CityPlace("lipetsk", "Липецк", "Lipetsk", 52.6102, 39.6658, mapPosDefault),
        CityPlace("kirov", "Киров", "Kirov", 58.6036, 49.6679, mapPosDefault),
        CityPlace("cheboksary", "Чебоксары", "Cheboksary", 56.1439, 47.2489, mapPosDefault),
        CityPlace("kaliningrad", "Калининград", "Kaliningrad", 54.7104, 20.4522, mapPosDefault),
        CityPlace("tula", "Тула", "Tula", 54.1931, 37.6177, mapPosDefault),
        CityPlace("kursk", "Курск", "Kursk", 51.7304, 36.1939, mapPosDefault),
        CityPlace("stavropol", "Ставрополь", "Stavropol", 45.0445, 41.9690, mapPosDefault),
        CityPlace("magnitogorsk", "Магнитогорск", "Magnitogorsk", 53.4186, 59.0472, mapPosDefault),
        CityPlace("tver", "Тверь", "Tver", 56.8587, 35.9176, mapPosDefault),
        CityPlace("murmansk", "Мурманск", "Murmansk", 68.9585, 33.0827, mapPosDefault),
        CityPlace("arkhangelsk", "Архангельск", "Arkhangelsk", 64.5399, 40.5158, mapPosDefault),
        CityPlace("kaluga", "Калуга", "Kaluga", 54.5293, 36.2754, mapPosDefault),
        CityPlace("smolensk", "Смоленск", "Smolensk", 54.7826, 32.0451, mapPosDefault),
        CityPlace("saransk", "Саранск", "Saransk", 54.1838, 45.1749, mapPosDefault),
        CityPlace("yoshkar-ola", "Йошкар-Ола", "Yoshkar-Ola", 56.6326, 47.8958, mapPosDefault),
        CityPlace("grozny", "Грозный", "Grozny", 43.3180, 45.6983, mapPosDefault),
        CityPlace("vladikavkaz", "Владикавказ", "Vladikavkaz", 43.0253, 44.6658, mapPosDefault),
        CityPlace("yuzhno-sakhalinsk", "Южно-Сахалинск", "Yuzhno-Sakhalinsk", 46.9591, 142.7380, mapPosDefault),
        CityPlace("petrozavodsk", "Петрозаводск", "Petrozavodsk", 61.7859, 34.3469, mapPosDefault),
        CityPlace("syktyvkar", "Сыктывкар", "Syktyvkar", 61.6688, 50.8364, mapPosDefault),
        CityPlace("chita", "Чита", "Chita", 52.0339, 113.4994, mapPosDefault),
        CityPlace("belgorod", "Белгород", "Belgorod", 50.5951, 36.5877, mapPosDefault),
        CityPlace("surgut", "Сургут", "Surgut", 61.2537, 73.3962, mapPosDefault),
        CityPlace("vladimir", "Владимир", "Vladimir", 56.1290, 40.4069, mapPosDefault),
        CityPlace("berlin", "Берлин", "Berlin", 52.5200, 13.4050, mapPosDefault),
        CityPlace("minsk", "Минск", "Minsk", 53.9045, 27.5615, mapPosDefault),
        CityPlace("astana", "Астана", "Astana", 51.1605, 71.4704, mapPosDefault)
    )

    init {
        val ids = places.map { it.id }
        require(ids.size == ids.toSet().size) {
            "WeatherCatalog: duplicate city id in places"
        }
    }

    private val stubForecastOffline = listOf(
        ForecastDay("Пн", "Mon", "cloud", 0, 5),
        ForecastDay("Вт", "Tue", "cloud", 0, 5),
        ForecastDay("Ср", "Wed", "cloud", 0, 5),
        ForecastDay("Чт", "Thu", "cloud", 0, 5),
        ForecastDay("Пт", "Fri", "cloud", 0, 5),
        ForecastDay("Сб", "Sat", "cloud", 0, 5),
        ForecastDay("Вс", "Sun", "cloud", 0, 5)
    )

    /** Заглушка, если сеть недоступна или не удалось подтянуть прогноз для конкретной точки. */
    fun fallbackCatalogItem(place: CityPlace): CityCatalogItem = CityCatalogItem(
        id = place.id,
        weather = WeatherInfo(
            city = place.city,
            cityEn = place.cityEn,
            temperature = 0,
            condition = "Нет данных",
            conditionEn = "No data",
            icon = "cloud",
            updatedAt = "—",
            wind = 0.0,
            humidity = 0,
            pressure = 0,
            sunrise = "--:--",
            sunset = "--:--"
        ),
        mapPosition = place.mapPosition,
        forecast = stubForecastOffline,
        latitude = place.latitude,
        longitude = place.longitude
    )

    fun offlineCities(): List<CityCatalogItem> = places.map { fallbackCatalogItem(it) }

    val homeCity: CityCatalogItem
        get() = offlineCities().first { it.id == HOME_CITY_ID }

    val cities: List<CityCatalogItem>
        get() = offlineCities()

    fun findCity(cityId: String): CityCatalogItem? = offlineCities().firstOrNull { it.id == cityId }

    fun findPlace(cityId: String): CityPlace? = places.firstOrNull { it.id == cityId }
}

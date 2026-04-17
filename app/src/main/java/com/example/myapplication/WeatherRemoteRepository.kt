package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

data class WeatherLoadResult(
    val cities: List<CityCatalogItem>,
    val usedNetwork: Boolean
)

class WeatherRemoteRepository(
    private val api: OpenMeteoApi = createDefaultOpenMeteoApi(),
    private val geocodingApi: OpenMeteoGeocodingApi = createDefaultGeocodingApi()
) {

    suspend fun loadForecastForPlace(place: CityPlace): CityCatalogItem? = withContext(Dispatchers.IO) {
        runCatching {
            api.getForecast(place.latitude, place.longitude)
        }.getOrNull()?.toCityCatalogItem(place)
    }

    suspend fun loadAllCities(): WeatherLoadResult = withContext(Dispatchers.IO) {
        val offline = WeatherCatalog.offlineCities()
        coroutineScope {
            val results = WeatherCatalog.places.map { place ->
                async {
                    runCatching {
                        api.getForecast(place.latitude, place.longitude)
                    }.getOrNull()?.toCityCatalogItem(place)
                }
            }.map { it.await() }
            if (results.any { it == null }) {
                WeatherLoadResult(offline, usedNetwork = false)
            } else {
                WeatherLoadResult(results.filterNotNull(), usedNetwork = true)
            }
        }
    }

    suspend fun searchPlaces(query: String, language: AppLanguage): List<GeocodingPlace> =
        withContext(Dispatchers.IO) {
            val q = query.trim()
            if (q.isEmpty()) return@withContext emptyList()
            runCatching {
                val langCode = when (language) {
                    AppLanguage.RU -> "ru"
                    AppLanguage.EN -> "en"
                }
                val response = geocodingApi.search(name = q, count = 10, language = langCode)
                response.results.orEmpty().mapNotNull { dto ->
                    val lat = dto.latitude ?: return@mapNotNull null
                    val lon = dto.longitude ?: return@mapNotNull null
                    val base = dto.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val suffix = listOfNotNull(
                        dto.admin1?.takeIf { it.isNotBlank() },
                        dto.country?.takeIf { it.isNotBlank() }
                    ).distinct().joinToString(", ")
                    val title = if (suffix.isNotEmpty()) "$base ($suffix)" else base
                    GeocodingPlace(title = title, latitude = lat, longitude = lon)
                }
            }.getOrElse { emptyList() }
        }

    companion object {
        private val sharedClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        }

        private fun createDefaultOpenMeteoApi(): OpenMeteoApi =
            Retrofit.Builder()
                .baseUrl(OpenMeteoApi.BASE_URL)
                .client(sharedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApi::class.java)

        private fun createDefaultGeocodingApi(): OpenMeteoGeocodingApi =
            Retrofit.Builder()
                .baseUrl(OpenMeteoGeocodingApi.BASE_URL)
                .client(sharedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoGeocodingApi::class.java)
    }
}

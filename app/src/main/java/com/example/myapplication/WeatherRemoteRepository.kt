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
    private val api: OpenMeteoApi = createDefaultOpenMeteoApi()
) {

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

    companion object {
        private fun createDefaultOpenMeteoApi(): OpenMeteoApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(OpenMeteoApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApi::class.java)
        }
    }
}

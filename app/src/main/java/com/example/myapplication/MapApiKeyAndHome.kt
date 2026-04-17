package com.example.myapplication

data class StoredMapApiKey(
    val id: String,
    val displayName: String,
    val apiKey: String
)

sealed class HomeCitySelection {
    data class Catalog(val cityId: String) : HomeCitySelection()
    data class Custom(
        val id: String,
        val latitude: Double,
        val longitude: Double,
        val nameRu: String,
        val nameEn: String
    ) : HomeCitySelection()
}

fun HomeCitySelection.Custom.toCityPlace(): CityPlace = CityPlace(
    id = id,
    city = nameRu,
    cityEn = nameEn,
    latitude = latitude,
    longitude = longitude,
    mapPosition = MapPosition(0.5f, 0.5f)
)

object HomeCityIds {
    fun customId(latitude: Double, longitude: Double): String {
        val a = (latitude * 100_000.0).toLong()
        val b = (longitude * 100_000.0).toLong()
        return "custom_${a}_$b"
    }
}

fun maskMapApiKey(key: String): String {
    val t = key.trim()
    if (t.length <= 8) return "••••"
    return "${t.take(4)}…${t.takeLast(4)}"
}

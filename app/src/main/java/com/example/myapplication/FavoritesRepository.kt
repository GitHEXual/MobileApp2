package com.example.myapplication

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val favoriteCityDao: FavoriteCityDao
) {
    val favorites: Flow<List<FavoriteCity>> = favoriteCityDao.getFavoritesFlow().map { entities ->
        entities.mapNotNull(FavoriteCityEntity::toFavoriteCity)
    }

    suspend fun addFavorite(cityId: String, note: String?) {
        favoriteCityDao.insertFavorite(
            FavoriteCityEntity(
                cityId = cityId,
                note = note.normalizedNote(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateFavoriteNote(cityId: String, note: String) {
        favoriteCityDao.updateNote(cityId, note.normalizedNote())
    }

    suspend fun deleteFavorite(cityId: String) {
        favoriteCityDao.deleteFavorite(cityId)
    }
}

fun String?.normalizedNote(): String? {
    return this?.trim()?.takeIf { it.isNotEmpty() }
}

fun FavoriteCityEntity.toFavoriteCity(): FavoriteCity? {
    val catalogItem = WeatherCatalog.findCity(cityId) ?: return null
    return FavoriteCity(
        cityId = catalogItem.id,
        weather = catalogItem.weather,
        note = note.normalizedNote(),
        createdAt = createdAt,
        mapPosition = catalogItem.mapPosition
    )
}

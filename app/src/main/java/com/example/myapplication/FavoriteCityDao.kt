package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY created_at DESC")
    fun getFavoritesFlow(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT * FROM favorite_cities WHERE city_id = :cityId LIMIT 1")
    suspend fun getFavoriteByCityId(cityId: String): FavoriteCityEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(entity: FavoriteCityEntity): Long

    @Query("UPDATE favorite_cities SET note = :note WHERE city_id = :cityId")
    suspend fun updateNote(cityId: String, note: String?)

    @Query("DELETE FROM favorite_cities WHERE city_id = :cityId")
    suspend fun deleteFavorite(cityId: String)
}

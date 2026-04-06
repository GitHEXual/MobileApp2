package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    val cityId: String,
    val note: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)

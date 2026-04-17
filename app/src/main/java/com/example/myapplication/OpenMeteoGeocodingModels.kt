package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class OpenMeteoGeocodingResponse(
    @SerializedName("results") val results: List<OpenMeteoGeocodingResultDto>?
)

data class OpenMeteoGeocodingResultDto(
    @SerializedName("name") val name: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("country") val country: String?,
    @SerializedName("admin1") val admin1: String?
)

data class GeocodingPlace(
    val title: String,
    val latitude: Double,
    val longitude: Double
)

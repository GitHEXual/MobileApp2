package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class OpenMeteoForecastResponse(
    @SerializedName("current") val current: OpenMeteoCurrentDto?,
    @SerializedName("daily") val daily: OpenMeteoDailyDto?
)

data class OpenMeteoCurrentDto(
    @SerializedName("time") val time: String?,
    @SerializedName("temperature_2m") val temperature2m: Double?,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Double?,
    @SerializedName("weather_code") val weatherCode: Int?,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double?,
    @SerializedName("surface_pressure") val surfacePressure: Double?
)

data class OpenMeteoDailyDto(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>?,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>?,
    @SerializedName("sunrise") val sunrise: List<String>?,
    @SerializedName("sunset") val sunset: List<String>?
)

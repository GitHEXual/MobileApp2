package com.example.myapplication

import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val apiDateTimeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
    isLenient = true
}

private val apiDateParser = SimpleDateFormat("yyyy-MM-dd", Locale.US)

private val updatedAtFormatter = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())

private val clockFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

fun wmoCodeToIcon(code: Int): String {
    return when (code) {
        0 -> "sun"
        1, 2 -> "cloud-sun"
        3 -> "cloud"
        in 45..48 -> "cloud"
        in 51..57 -> "cloud-rain"
        in 61..67 -> "rain"
        in 71..77 -> "cloud"
        in 80..82 -> "rain"
        in 85..86 -> "cloud"
        in 95..99 -> "rain"
        else -> "cloud"
    }
}

fun wmoCodeToConditions(code: Int): Pair<String, String> {
    return when (code) {
        0 -> "Ясно" to "Clear"
        1 -> "Преимущественно ясно" to "Mainly clear"
        2 -> "Переменная облачность" to "Partly cloudy"
        3 -> "Пасмурно" to "Overcast"
        in 45..48 -> "Туман" to "Fog"
        in 51..55 -> "Морось" to "Drizzle"
        in 56..57 -> "Ледяная морось" to "Freezing drizzle"
        in 61..65 -> "Дождь" to "Rain"
        in 66..67 -> "Ледяной дождь" to "Freezing rain"
        in 71..75 -> "Снег" to "Snow"
        77 -> "Снежные зёрна" to "Snow grains"
        in 80..82 -> "Ливень" to "Rain showers"
        in 85..86 -> "Снегопад" to "Snow showers"
        95 -> "Гроза" to "Thunderstorm"
        in 96..99 -> "Гроза с градом" to "Thunderstorm with hail"
        else -> "Облачно" to "Cloudy"
    }
}

fun formatOpenMeteoUpdatedAt(isoTime: String?): String {
    if (isoTime.isNullOrBlank()) return ""
    return try {
        val date = apiDateTimeParser.parse(isoTime.trim()) ?: return isoTime
        updatedAtFormatter.format(date)
    } catch (_: Exception) {
        isoTime
    }
}

fun formatClockFromIso(iso: String?): String {
    if (iso.isNullOrBlank()) return "--:--"
    return try {
        val trimmed = iso.trim()
        val parsed: Date? = when {
            trimmed.contains("T") ->
                parseOffsetDateTime(trimmed)
                    ?: apiDateTimeParser.parse(trimmed.take(16))
            else -> apiDateParser.parse(trimmed)
        }
        val date = parsed ?: return "--:--"
        clockFormatter.format(date)
    } catch (_: Exception) {
        "--:--"
    }
}

private fun parseOffsetDateTime(iso: String): Date? {
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
        fmt.parse(iso)
    } catch (_: Exception) {
        try {
            val fmt2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            fmt2.parse(iso)
        } catch (_: Exception) {
            null
        }
    }
}

fun forecastDayLabelRuEn(dateYmd: String): Pair<String, String> {
    return try {
        val date = apiDateParser.parse(dateYmd) ?: return "—" to "—"
        val ruLocale = Locale.forLanguageTag("ru")
        val ru = SimpleDateFormat("EEE", ruLocale).format(date).replaceFirstChar { it.uppercase() }
        val en = SimpleDateFormat("EEE", Locale.ENGLISH).format(date).replaceFirstChar { it.uppercase() }
        ru to en
    } catch (_: Exception) {
        "—" to "—"
    }
}

fun OpenMeteoForecastResponse.toCityCatalogItem(place: CityPlace): CityCatalogItem? {
    val cur = current ?: return null
    val temp = cur.temperature2m ?: return null
    val code = cur.weatherCode ?: return null
    val humidity = cur.relativeHumidity2m?.roundToInt() ?: return null
    val wind = cur.windSpeed10m ?: return null
    val pressure = cur.surfacePressure?.toInt() ?: return null

    val (condRu, condEn) = wmoCodeToConditions(code)
    val icon = wmoCodeToIcon(code)
    val updatedAt = formatOpenMeteoUpdatedAt(cur.time)

    val sunriseToday = daily?.sunrise?.firstOrNull()
    val sunsetToday = daily?.sunset?.firstOrNull()

    val forecast = buildForecast(daily)

    return CityCatalogItem(
        id = place.id,
        weather = WeatherInfo(
            city = place.city,
            cityEn = place.cityEn,
            temperature = temp.toInt(),
            condition = condRu,
            conditionEn = condEn,
            icon = icon,
            updatedAt = updatedAt,
            wind = (wind * 10).toInt() / 10.0,
            humidity = humidity,
            pressure = pressure,
            sunrise = formatClockFromIso(sunriseToday),
            sunset = formatClockFromIso(sunsetToday)
        ),
        mapPosition = place.mapPosition,
        forecast = forecast,
        latitude = place.latitude,
        longitude = place.longitude
    )
}

private fun buildForecast(daily: OpenMeteoDailyDto?): List<ForecastDay> {
    val times = daily?.time ?: return emptyList()
    val codes = daily.weatherCode ?: return emptyList()
    val maxT = daily.temperature2mMax ?: return emptyList()
    val minT = daily.temperature2mMin ?: return emptyList()
    val count = minOf(times.size, codes.size, maxT.size, minT.size)
    if (count == 0) return emptyList()
    return (0 until count).map { i ->
        val (dayRu, dayEn) = forecastDayLabelRuEn(times[i])
        ForecastDay(
            day = dayRu,
            dayEn = dayEn,
            icon = wmoCodeToIcon(codes[i]),
            minTemp = minT[i].toInt(),
            maxTemp = maxT[i].toInt()
        )
    }
}

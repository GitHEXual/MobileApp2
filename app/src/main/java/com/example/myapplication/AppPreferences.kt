package com.example.myapplication

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("weather_app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val mapKeysListType = object : TypeToken<List<StoredMapApiKey>>() {}.type

    fun loadLanguage(): AppLanguage {
        return runCatching {
            AppLanguage.valueOf(
                preferences.getString("language", AppLanguage.RU.name) ?: AppLanguage.RU.name
            )
        }.getOrDefault(AppLanguage.RU)
    }

    fun saveLanguage(value: AppLanguage) {
        preferences.edit().putString("language", value.name).apply()
    }

    fun loadTheme(): AppThemeMode {
        return runCatching {
            AppThemeMode.valueOf(
                preferences.getString("theme", AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
            )
        }.getOrDefault(AppThemeMode.LIGHT)
    }

    fun saveTheme(value: AppThemeMode) {
        preferences.edit().putString("theme", value.name).apply()
    }

    fun resolveActiveMapKitApiKey(): String {
        val id = loadActiveMapKeyId()
        if (id == MapKitConfig.BUILTIN_KEY_ID) return MapKitConfig.DEFAULT_API_KEY
        return loadMapApiKeys().firstOrNull { it.id == id }?.apiKey ?: MapKitConfig.DEFAULT_API_KEY
    }

    fun loadActiveMapKeyId(): String =
        preferences.getString(KEY_ACTIVE_MAP_KEY, MapKitConfig.BUILTIN_KEY_ID) ?: MapKitConfig.BUILTIN_KEY_ID

    fun loadMapApiKeys(): List<StoredMapApiKey> {
        val raw = preferences.getString(KEY_MAP_API_KEYS_JSON, null) ?: return emptyList()
        return runCatching { gson.fromJson<List<StoredMapApiKey>>(raw, mapKeysListType) }.getOrDefault(emptyList())
    }

    fun saveMapApiKeys(keys: List<StoredMapApiKey>) {
        preferences.edit().putString(KEY_MAP_API_KEYS_JSON, gson.toJson(keys)).apply()
    }

    fun setActiveMapKeyId(id: String) {
        preferences.edit().putString(KEY_ACTIVE_MAP_KEY, id).apply()
    }

    fun addMapApiKey(displayName: String, apiKey: String): StoredMapApiKey {
        val key = StoredMapApiKey(
            id = UUID.randomUUID().toString(),
            displayName = displayName.trim().ifBlank { "API key" },
            apiKey = apiKey.trim()
        )
        saveMapApiKeys(loadMapApiKeys() + key)
        return key
    }

    fun removeMapApiKey(id: String) {
        saveMapApiKeys(loadMapApiKeys().filterNot { it.id == id })
        if (loadActiveMapKeyId() == id) {
            setActiveMapKeyId(MapKitConfig.BUILTIN_KEY_ID)
        }
    }

    fun loadHomeCitySelection(): HomeCitySelection {
        val raw = preferences.getString(KEY_HOME_CITY_JSON, null)
            ?: return HomeCitySelection.Catalog(WeatherCatalog.HOME_CITY_ID)
        val dto = runCatching { gson.fromJson(raw, HomeCityJson::class.java) }.getOrNull()
            ?: return HomeCitySelection.Catalog(WeatherCatalog.HOME_CITY_ID)
        return when (dto.mode) {
            "custom" -> {
                val id = dto.id ?: return HomeCitySelection.Catalog(WeatherCatalog.HOME_CITY_ID)
                val lat = dto.lat ?: return HomeCitySelection.Catalog(WeatherCatalog.HOME_CITY_ID)
                val lon = dto.lon ?: return HomeCitySelection.Catalog(WeatherCatalog.HOME_CITY_ID)
                val ru = dto.nameRu ?: "—"
                val en = dto.nameEn ?: "—"
                HomeCitySelection.Custom(id, lat, lon, ru, en)
            }
            else -> HomeCitySelection.Catalog(dto.cityId ?: WeatherCatalog.HOME_CITY_ID)
        }
    }

    fun saveHomeCitySelection(selection: HomeCitySelection) {
        val json = when (selection) {
            is HomeCitySelection.Catalog -> gson.toJson(
                HomeCityJson(mode = "catalog", cityId = selection.cityId)
            )
            is HomeCitySelection.Custom -> gson.toJson(
                HomeCityJson(
                    mode = "custom",
                    id = selection.id,
                    lat = selection.latitude,
                    lon = selection.longitude,
                    nameRu = selection.nameRu,
                    nameEn = selection.nameEn
                )
            )
        }
        preferences.edit().putString(KEY_HOME_CITY_JSON, json).apply()
    }

    private data class HomeCityJson(
        val mode: String,
        val cityId: String? = null,
        val id: String? = null,
        val lat: Double? = null,
        val lon: Double? = null,
        val nameRu: String? = null,
        val nameEn: String? = null
    )

    companion object {
        private const val KEY_ACTIVE_MAP_KEY = "active_map_key_id"
        private const val KEY_MAP_API_KEYS_JSON = "map_api_keys_json"
        private const val KEY_HOME_CITY_JSON = "home_city_json"
    }
}

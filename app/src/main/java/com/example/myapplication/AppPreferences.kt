package com.example.myapplication

import android.content.Context

class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("weather_app_prefs", Context.MODE_PRIVATE)

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
}

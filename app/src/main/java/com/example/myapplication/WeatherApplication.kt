package com.example.myapplication

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = AppPreferences(this)
        MapKitFactory.setApiKey(prefs.resolveActiveMapKitApiKey())
        MapKitFactory.initialize(this)
    }
}

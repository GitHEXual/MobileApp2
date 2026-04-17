package com.example.myapplication

import android.app.Application
import android.content.Context
import org.osmdroid.config.Configuration

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        Configuration.getInstance().load(this, prefs)
        Configuration.getInstance().userAgentValue = packageName
    }
}

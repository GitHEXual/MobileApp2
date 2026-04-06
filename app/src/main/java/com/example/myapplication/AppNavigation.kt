package com.example.myapplication

object AppRoutes {
    const val HOME = "home"
    const val MAP = "map"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{cityId}"

    fun detail(cityId: String): String = "detail/$cityId"
}

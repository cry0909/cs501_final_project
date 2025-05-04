package com.example.wellipet.api

// WeatherResponse.kt
data class WeatherResponse(
    val name: String, // cityName
    val main: MainInfo,
    val weather: List<WeatherInfo>
)

data class MainInfo(
    val temp: Double, // Temp
    val humidity: Int
)

data class WeatherInfo(
    val description: String, // Weather Description
    val icon: String         // Icon for Weather
)

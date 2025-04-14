package com.example.wellipet.api

// WeatherResponse.kt
data class WeatherResponse(
    val name: String, // 城市名称
    val main: MainInfo,
    val weather: List<WeatherInfo>
)

data class MainInfo(
    val temp: Double, // 温度
    val humidity: Int
)

data class WeatherInfo(
    val description: String, // 天气描述，如"clear sky"
    val icon: String         // 用于天气图标展示的代码
)

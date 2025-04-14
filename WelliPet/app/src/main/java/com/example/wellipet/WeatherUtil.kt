package com.example.wellipet


import com.example.wellipet.R

fun getWeatherIconRes(weatherDescription: String): Int {
    return when {
        weatherDescription.contains("clear sky", ignoreCase = true) -> R.drawable.sunny
        weatherDescription.contains("rain", ignoreCase = true) -> R.drawable.rain
        weatherDescription.contains("cloud", ignoreCase = true) -> R.drawable.cloud
        // 可根据实际需要添加更多情况
        else -> R.drawable.cloud
    }
}

/**
 * 根据天气描述返回建议的运动提示文字
 */
fun getSuggestionText(weatherDescription: String): String {
    return when {
        weatherDescription.contains("clear sky", ignoreCase = true) -> "good for outdoor activities"
        weatherDescription.contains("rain", ignoreCase = true) -> "good for indoor activities"
        weatherDescription.contains("cloud", ignoreCase = true) -> "the climate might change overtime"
        else -> "unknown"
    }
}

package com.example.wellipet.utils


import com.example.wellipet.R

fun getWeatherIconRes(weatherDescription: String): Int {
    return when {
        weatherDescription.contains("clear sky", ignoreCase = true) -> R.drawable.sun
        weatherDescription.contains("few clouds", ignoreCase = true) -> R.drawable.fewclouds
        weatherDescription.contains("scattered clouds", ignoreCase = true) -> R.drawable.scaclouds
        weatherDescription.contains("broken clouds", ignoreCase = true) -> R.drawable.bclouds
        weatherDescription.contains("shower rain", ignoreCase = true) -> R.drawable.srain
        weatherDescription.contains("rain", ignoreCase = true) -> R.drawable.rain
        weatherDescription.contains("thunderstorm", ignoreCase = true) -> R.drawable.storm
        weatherDescription.contains("snow", ignoreCase = true) -> R.drawable.snow
        weatherDescription.contains("mist", ignoreCase = true) -> R.drawable.mist

        else -> R.drawable.scaclouds
    }
}

/**
 * Returns suggested activity tips based on the weather description.
 */
fun getSuggestionText(weatherDescription: String): String {
    return when {
        weatherDescription.contains("thunderstorm", ignoreCase = true) ->
            "Thunderstorms expected. Best to stay indoors."

        // Light drizzle
        weatherDescription.contains("drizzle", ignoreCase = true) ->
            "Light drizzle. You might want an umbrella or a waterproof jacket."

        // Showers / Rain
        weatherDescription.contains("shower rain", ignoreCase = true) ->
            "Showers in the forecast. Consider indoor activities."
        weatherDescription.contains("rain", ignoreCase = true) ->
            "Rainy conditions. Indoor exercise is recommended."

        // Snow
        weatherDescription.contains("snow", ignoreCase = true) ->
            "Snowfall occurring. Stay warm and consider indoor workouts."

        // Fog / Mist / Haze
        weatherDescription.contains("mist", ignoreCase = true) ||
                weatherDescription.contains("fog", ignoreCase = true) ||
                weatherDescription.contains("haze", ignoreCase = true) ->
            "Low visibility due to fog or haze. Be cautious outside, indoor is safer."

        // Dust / Sand / Ash
        weatherDescription.contains("dust", ignoreCase = true) ||
                weatherDescription.contains("sand", ignoreCase = true) ||
                weatherDescription.contains("ash", ignoreCase = true) ->
            "Poor air quality. Best to stay indoors."

        // Clear sky
        weatherDescription.contains("clear sky", ignoreCase = true) ->
            "Clear skies! Perfect for outdoor activities."

        // Clouds
        weatherDescription.contains("few clouds", ignoreCase = true) ||
                weatherDescription.contains("scattered clouds", ignoreCase = true) ||
                weatherDescription.contains("broken clouds", ignoreCase = true) ||
                weatherDescription.contains("clouds", ignoreCase = true) ->
            "Partly cloudy. Outdoor activities are fine—bring a light layer just in case."

        // Fallback
        else ->
            "Weather unclear. Choose indoor or outdoor activities based on your preference."
    }
}

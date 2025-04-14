package com.example.wellipet.api

// WeatherViewModel.kt
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState
    private val apiKey = "Your API Key"

    // 假设你已经初始化 RetrofitClient.weatherService
    fun fetchWeatherByLocation() {
        viewModelScope.launch {
            try {
                val locationRepo = LocationRepository(getApplication())
                val coordinates = locationRepo.getCurrentLocation()
                if (coordinates != null) {
                    val (lat, lon) = coordinates
                    val response = RetrofitClient.weatherService.getCurrentWeatherByCoordinates(lat, lon, apiKey)
                    _weatherState.value = response
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 错误处理
            }
        }
    }
}

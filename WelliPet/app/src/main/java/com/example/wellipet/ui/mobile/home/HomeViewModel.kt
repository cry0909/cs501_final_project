// File: com/example/wellipet/ui/mobile/home/HomeViewModel.kt
package com.example.wellipet.ui.mobile.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.api.LocationRepository
import com.example.wellipet.api.RetrofitClient
import com.example.wellipet.api.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val weatherService = RetrofitClient.weatherService
    private val apiKey = "168c570c1ea3755876596d9b6fc3ea76"

    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: StateFlow<WeatherResponse?> = _weatherResponse

    init {
        viewModelScope.launch {
            // 每當位置更新時自動抓取新的天氣資訊
            locationRepository.getLocationFlow().collectLatest { coordinates ->
                if (coordinates != null) {
                    val (lat, lon) = coordinates
                    try {
                        val response = weatherService.getCurrentWeatherByCoordinates(lat, lon, apiKey)
                        _weatherResponse.value = response
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // 如果無法取得位置，使用預設城市查詢（例如 "Beijing"）
                    try {
                        val response = weatherService.getCurrentWeatherByCity("Beijing", apiKey)
                        _weatherResponse.value = response
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}

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
import kotlinx.coroutines.launch
import com.example.wellipet.data.repository.FirebaseUserRepository
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val weatherService = RetrofitClient.weatherService
    private val apiKey = "168c570c1ea3755876596d9b6fc3ea76"

    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: StateFlow<WeatherResponse?> = _weatherResponse


    private val userRepo = FirebaseUserRepository()
    private val healthRepo = HealthRepository(application)

    val petStatus: StateFlow<String?> =
        userRepo.petStatusFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, "happy")

    /** 把计算状态的逻辑抽成一个 suspend 函数 */
    private suspend fun computeAndSavePetStatus() {
        // 过去 1h 的喝水量
        val water1h = runCatching { healthRepo.getHydrationLast(hours = 1) }.getOrDefault(0L)
        // 过去 2h 的步数
        val steps2h = runCatching { healthRepo.getStepsLast(hours = 2)    }.getOrDefault(0L)

        val newStatus = when {
            water1h < 100L -> "thirsty"
            steps2h <  25L -> "sleepy"
            else           -> "happy"
        }
        userRepo.savePetStatus(newStatus)
    }

    init { viewModelScope.launch { computeAndSavePetStatus() }} //  启动时立刻刷新一次

    /** 对外暴露，供喝水／走路后立即调用 */
    fun refreshPetStatusNow() {
        viewModelScope.launch { computeAndSavePetStatus() }
    }

    /** 只有在确认为有位置权限后才调用这个方法，开始订阅位置并拉天气(AI solution) */
    fun loadWeather() {
        viewModelScope.launch {
            locationRepository.getLocationFlow().collect { coords ->
                val response = try {
                    if (coords != null) {
                        val (lat, lon) = coords
                        weatherService.getCurrentWeatherByCoordinates(lat, lon, apiKey)
                    } else {
                        weatherService.getCurrentWeatherByCity("Beijing", apiKey)
                    }
                } catch (e: Exception) {
                    null
                }
                _weatherResponse.value = response
            }
        }
    }
}



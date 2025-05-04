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

    /** Extracts the logic for computing pet status into a suspend function */
    private suspend fun computeAndSavePetStatus() {
        // Amount of water consumed in the past hour
        val water1h = runCatching { healthRepo.getHydrationLast(hours = 1) }.getOrDefault(0L)
        // Step count in the past two hours
        val steps2h = runCatching { healthRepo.getStepsLast(hours = 2)    }.getOrDefault(0L)

        val newStatus = when {
            water1h < 100L -> "thirsty"
            steps2h <  25L -> "sleepy"
            else           -> "happy"
        }
        userRepo.savePetStatus(newStatus)
    }

    init { viewModelScope.launch { computeAndSavePetStatus() }} // Refresh once immediately on initialization

    /** Public function to trigger an immediate pet status update after hydration or steps changes */
    fun refreshPetStatusNow() {
        viewModelScope.launch { computeAndSavePetStatus() }
    }

    /**
     * Should be called only when location permission is granted;
     * subscribes to location updates and fetches weather data (AI solution)
     */
    fun loadWeather() {
        viewModelScope.launch {
            locationRepository.getLocationFlow().collect { coords ->
                val response = try {
                    if (coords != null) {
                        val (lat, lon) = coords
                        weatherService.getCurrentWeatherByCoordinates(lat, lon, apiKey)
                    } else {
                        weatherService.getCurrentWeatherByCity("Boston", apiKey)
                    }
                } catch (e: Exception) {
                    null
                }
                _weatherResponse.value = response
            }
        }
    }
}



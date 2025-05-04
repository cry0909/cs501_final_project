// File: com/example/wellipet/ui/mobile/healthdata/HealthDataViewModel.kt
package com.example.wellipet.ui.mobile.healthdata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthDataViewModel(application: Application) : AndroidViewModel(application) {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val repository = HealthRepository(application)

    private val _currentSteps = MutableStateFlow(0L)
    val currentSensorSteps: StateFlow<Long> = _currentSteps

    // Stores total sleep (sum of all sleep sessions in the past 24 hours, in seconds)
    private val _currentSleep = MutableStateFlow(0L)
    val currentSleep: StateFlow<Long> = _currentSleep

    // Stores total hydration (sum of all hydration records in the past 24 hours, in mL)
    private val _currentHydration = MutableStateFlow(0L)
    val currentHydration: StateFlow<Long> = _currentHydration

    // Historical data: list of Pair<date, value>
    private val _historicalSteps = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalSteps: StateFlow<List<Pair<String, Long>>> = _historicalSteps

    private val _historicalSleep = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalSleep: StateFlow<List<Pair<String, Long>>> = _historicalSleep

    private val _historicalHydration = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalHydration: StateFlow<List<Pair<String, Long>>> = _historicalHydration


    // Read all data from Health Connect (steps, sleep, hydration)
    fun readHealthData(rangeDays: Int = 7) {
        viewModelScope.launch {
            try {
                repository.clearHistoryCache()

                _currentSteps.value = repository.getSteps()
                _currentSleep.value = repository.readSleep()
                _currentHydration.value = repository.readHydration()
                _historicalSteps.value = repository.getHistoricalSteps(rangeDays)
                _historicalSleep.value = repository.getHistoricalSleep(rangeDays)
                _historicalHydration.value = repository.getHistoricalHydration(rangeDays)
            } catch (e: Exception) {
                _errorMessage.value = "load health data failed"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

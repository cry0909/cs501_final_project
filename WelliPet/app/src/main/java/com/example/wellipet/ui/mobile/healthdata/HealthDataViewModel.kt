// File: com/example/wellipet/ui/mobile/healthdata/HealthDataViewModel.kt
package com.example.wellipet.ui.mobile.healthdata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellipet.data.model.StepCount
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

    // 儲存睡眠資料（過去24小時內所有睡眠記錄的總時長，以秒計算）
    private val _currentSleep = MutableStateFlow(0L)
    val currentSleep: StateFlow<Long> = _currentSleep

    // 儲存飲水資料（過去24小時內所有飲水記錄的水量總和，以毫升計算）
    private val _currentHydration = MutableStateFlow(0L)
    val currentHydration: StateFlow<Long> = _currentHydration

    // 歷史資料：使用 Pair<日期, 數值> 列表
    private val _historicalSteps = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalSteps: StateFlow<List<Pair<String, Long>>> = _historicalSteps

    private val _historicalSleep = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalSleep: StateFlow<List<Pair<String, Long>>> = _historicalSleep

    private val _historicalHydration = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val historicalHydration: StateFlow<List<Pair<String, Long>>> = _historicalHydration


    // 讀取 Health Connect 上的所有數據（步數、睡眠、飲水）
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

    fun addHydration(hydrationMl: Long) {
        viewModelScope.launch {
            if (repository.addHydration(hydrationMl)) {
                readHealthData(7)
            }
        }
    }


    fun readSensorSteps() {
        viewModelScope.launch {
            // 讀取感應器步數
            val steps = repository.getSensorSteps()
            _currentSteps.value = steps
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

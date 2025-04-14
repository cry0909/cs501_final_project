// File: com/example/wellipet/api/LocationRepository.kt
package com.example.wellipet.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // 持續訂閱位置更新
    @SuppressLint("MissingPermission")
    fun getLocationFlow() = callbackFlow<Pair<Double, Double>?> {
        // 設定位置更新參數
        val locationRequest = LocationRequest.create().apply {
            interval = 10000  // 每10秒更新一次
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation?.let { Pair(it.latitude, it.longitude) })
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, callback, null)
        awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
    }


}

package com.example.wellipet.api

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationRepository(private val context: Context) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        // 初始化 FusedLocationProviderClient
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // 获取上一次的位置信息
        val location = fusedLocationProviderClient.lastLocation.await()
        return location?.let { Pair(it.latitude, it.longitude) }
    }
}

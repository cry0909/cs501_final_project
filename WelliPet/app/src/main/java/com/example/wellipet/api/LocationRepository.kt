// File: com/example/wellipet/api/LocationRepository.kt
package com.example.wellipet.api

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Keep track of the last location
    @SuppressLint("MissingPermission")
    fun getLocationFlow() = callbackFlow<Pair<Double, Double>?> {
        // Set up the location request
        val locationRequest = LocationRequest.create().apply {
            interval = 10000  // updates every 10 sec
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

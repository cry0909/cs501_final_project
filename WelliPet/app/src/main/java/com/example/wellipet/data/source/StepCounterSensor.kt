// File: com/example/wellipet/data/source/StepCounterSensor.kt
package com.example.wellipet.data.source

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val TAG = "StepCounterSensor"

class StepCounterSensor(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    suspend fun getSteps(): Long = suspendCancellableCoroutine { continuation ->
        if (stepSensor == null) {
            Log.d(TAG, "Step counter sensor is not present on this device")
            continuation.resume(0L)
            return@suspendCancellableCoroutine
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val steps = event.values[0].toLong()
                Log.d(TAG, "Steps since last reboot: $steps")
                if (continuation.isActive) {
                    continuation.resume(steps)
                    sensorManager.unregisterListener(this)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "Sensor accuracy changed to: $accuracy")
            }
        }
        sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }
}

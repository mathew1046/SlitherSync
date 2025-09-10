package com.malabarmatrix.slithersync.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SensorRepository {
    val headingDegrees: Flow<Float>
}

class AndroidSensorRepository(
    private val context: Context,
    private val smoothingAlpha: Float = 0.12f
) : SensorRepository {
    override val headingDegrees: Flow<Float> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotation = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        var smoothed: Float? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR || event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    if (azimuth < 0) azimuth += 360f
                    smoothed = smoothed?.let { it + smoothingAlpha * (azimuth - it) } ?: azimuth
                    trySend(smoothed ?: azimuth)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotation != null) {
            sensorManager.registerListener(listener, rotation, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

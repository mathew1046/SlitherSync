package com.malabarmatrix.slithersync.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs
import kotlin.math.sqrt

class StepSensorManager(private val context: Context) {
    // Emits 1 for each detected step
    val stepEvents: Flow<Int> = callbackFlow {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepDetector = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val linearAcc = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Enhanced sensor fusion parameters
        var lastStepTimeNs = 0L
        var lastPeakTimeNs = 0L
        var prevMagnitude = 0f
        var smoothedMagnitude = 0f
        // Adaptive thresholding (EWMA of delta and absolute deviation)
        var ewmaDelta = 0f
        var ewmaAbsDev = 0f
        // Gyro-derived shake suppression
        var lastGyroMagnitude = 0f
        val gyroShakeThreshold = 4.0f // rad/s, above this likely a hand shake
        
        // Debounce and filtering parameters
        val minStepIntervalNs = 220_000_000L // absolute minimum interval between steps
        val peakDebounceNs = 180_000_000L // 180ms debounce for accel peaks
        val smoothingAlpha = 0.25f // More responsive low-pass
        val ewmaAlpha = 0.2f
        // Adaptive cadence window (based on last inter-step intervals)
        var ewmaIntervalNs = 600_000_000L // start at 600ms (~100 steps/min)
        val intervalAlpha = 0.2f
        
        // Step detection state
        var stepDetectorActive = false
        var accelerometerActive = false
        var consecutiveValidReadings = 0
        val minConsecutiveReadings = 2

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_STEP_DETECTOR -> {
                        val now = event.timestamp
                        // Primary step detector - most reliable, emit immediately within cadence constraints
                        val minInterval = maxOf(minStepIntervalNs, (ewmaIntervalNs * 0.45f).toLong())
                        if (now - lastStepTimeNs > minInterval) {
                            lastStepTimeNs = now
                            stepDetectorActive = true
                            // update cadence estimate using step detector
                            val interval = if (ewmaIntervalNs > 0) now - (now - ewmaIntervalNs) else ewmaIntervalNs
                            ewmaIntervalNs = if (ewmaIntervalNs == 0L) interval else (ewmaIntervalNs + (intervalAlpha * (interval - ewmaIntervalNs)).toLong())
                            trySend(1)
                        }
                    }
                    Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ACCELEROMETER -> {
                        // Accelerometer-based fallback (prefer linear acceleration when available)
                        val ax = event.values[0]
                        val ay = event.values[1]
                        val az = event.values[2]

                        // Use magnitude of linear acceleration (gravity largely removed for TYPE_LINEAR_ACCELERATION)
                        val magnitude = sqrt(ax * ax + ay * ay + az * az)

                        // Low-pass to smooth spikes while remaining responsive
                        smoothedMagnitude = if (smoothedMagnitude == 0f) magnitude
                            else smoothedMagnitude + smoothingAlpha * (magnitude - smoothedMagnitude)

                        val magnitudeDelta = abs(smoothedMagnitude - prevMagnitude)
                        val now = event.timestamp

                        // Update adaptive threshold statistics
                        ewmaDelta = if (ewmaDelta == 0f) magnitudeDelta else ewmaDelta + ewmaAlpha * (magnitudeDelta - ewmaDelta)
                        val absDev = abs(magnitudeDelta - ewmaDelta)
                        ewmaAbsDev = if (ewmaAbsDev == 0f) absDev else ewmaAbsDev + ewmaAlpha * (absDev - ewmaAbsDev)

                        // Dynamic threshold: baseline + 2.2 * deviation, with floors/caps
                        val dynamicThreshold = (ewmaDelta + 2.2f * ewmaAbsDev).coerceIn(0.5f, 2.8f)

                        if (
                            // Use accel path both when detector missing and as backup for missed steps
                            magnitudeDelta > dynamicThreshold &&
                            now - lastPeakTimeNs > peakDebounceNs &&
                            now - lastStepTimeNs > maxOf(minStepIntervalNs, (ewmaIntervalNs * 0.45f).toLong()) &&
                            // suppress obvious shakes based on gyro magnitude
                            lastGyroMagnitude < gyroShakeThreshold
                        ) {
                            consecutiveValidReadings++
                            if (consecutiveValidReadings >= minConsecutiveReadings) {
                                lastPeakTimeNs = now
                                lastStepTimeNs = now
                                accelerometerActive = true
                                consecutiveValidReadings = 0
                                // update cadence estimate
                                if (ewmaIntervalNs == 0L) ewmaIntervalNs = 600_000_000L else ewmaIntervalNs = (ewmaIntervalNs + (intervalAlpha * ((now - lastPeakTimeNs) - ewmaIntervalNs)).toLong())
                                trySend(1)
                            }
                        } else if (magnitudeDelta <= dynamicThreshold) {
                            consecutiveValidReadings = maxOf(0, consecutiveValidReadings - 1)
                        }

                        prevMagnitude = smoothedMagnitude
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        // Track recent angular velocity magnitude to help suppress shake-induced false positives
                        val gx = event.values[0]
                        val gy = event.values[1]
                        val gz = event.values[2]
                        lastGyroMagnitude = sqrt(gx * gx + gy * gy + gz * gz)
                    }
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
            }
        }

        // Register all useful sensors for fusion: STEP_DETECTOR primary, plus linear accel/accelerometer and gyro for responsiveness
        if (stepDetector != null) {
            manager.registerListener(listener, stepDetector, SensorManager.SENSOR_DELAY_GAME)
        }
        if (linearAcc != null) {
            manager.registerListener(listener, linearAcc, SensorManager.SENSOR_DELAY_GAME)
        }
        if (accelerometer != null) {
            manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
        if (gyroscope != null) {
            manager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose { 
            manager.unregisterListener(listener) 
        }
    }
}


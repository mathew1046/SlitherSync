package com.malabarmatrix.slithersync.data

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StepRepository {
    val stepDeltaFlow: Flow<Int>
    suspend fun ensurePermissions(activity: Activity): Boolean
    suspend fun startRecording(context: Context)
    suspend fun readDailyTotal(context: Context): Int
}

class FitStepRepository(private val stepPixelMultiplier: Float) : StepRepository {
    private val _delta = MutableStateFlow(0)
    override val stepDeltaFlow: Flow<Int> = _delta

    private val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .build()

    override suspend fun ensurePermissions(activity: Activity): Boolean {
        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        val hasPerms = com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions(account, fitnessOptions)
        if (!hasPerms) {
            com.google.android.gms.auth.api.signin.GoogleSignIn.requestPermissions(activity, 1001, account, fitnessOptions)
            return false
        }
        return true
    }

    override suspend fun startRecording(context: Context) {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnSuccessListener { }
            .addOnFailureListener { }
        // For simplicity in this prototype, rely on periodic History reads in ViewModel or activity lifecycle.
    }

    override suspend fun readDailyTotal(context: Context): Int {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        val client = Fitness.getHistoryClient(context, account)
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            client.readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener { response ->
                    val total = if (response.isEmpty) 0 else response.dataPoints.first().getValue(DataType.TYPE_STEP_COUNT_DELTA.fields[0]).asInt()
                    if (cont.isActive) cont.resume(total) {}
                }
                .addOnFailureListener { _ -> if (cont.isActive) cont.resume(0) {} }
        }
    }
}

class SimulatedStepRepository : StepRepository {
    private val _delta = MutableStateFlow(0)
    override val stepDeltaFlow: Flow<Int> = _delta

    fun emitSteps(delta: Int) { _delta.value = delta }

    override suspend fun ensurePermissions(activity: Activity): Boolean = true

    override suspend fun startRecording(context: Context) {}

    override suspend fun readDailyTotal(context: Context): Int = 0
}

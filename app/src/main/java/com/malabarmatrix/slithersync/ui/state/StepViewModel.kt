package com.malabarmatrix.slithersync.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malabarmatrix.slithersync.data.StepSensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {
    
    private val stepSensorManager = StepSensorManager(application)
    
    // Step tracking
    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps
    
    private val _sessionSteps = MutableStateFlow(0)
    val sessionSteps: StateFlow<Int> = _sessionSteps
    
    // Calories calculation (steps × 0.04 kcal per step)
    private val _caloriesBurned = MutableStateFlow(0.0)
    val caloriesBurned: StateFlow<Double> = _caloriesBurned
    
    // Step detection state
    private val _isStepDetectionActive = MutableStateFlow(false)
    val isStepDetectionActive: StateFlow<Boolean> = _isStepDetectionActive
    
    // Per-step event stream: emits 1 for each detected step immediately
    private val _stepEvents = MutableSharedFlow<Int>(extraBufferCapacity = 32)
    val stepEvents: SharedFlow<Int> = _stepEvents
    
    init {
        startStepDetection()
    }
    
    private fun startStepDetection() {
        _isStepDetectionActive.value = true
        
        viewModelScope.launch {
            stepSensorManager.stepEvents.collectLatest { stepCount ->
                // Update step counts
                _totalSteps.value += stepCount
                _sessionSteps.value += stepCount
                
                // Calculate calories (0.04 kcal per step)
                _caloriesBurned.value = _sessionSteps.value * 0.04
                
                // Emit step event(s) for game movement immediately
                repeat(stepCount) { _ -> _stepEvents.tryEmit(1) }
            }
        }
    }
    
    fun resetSession() {
        _sessionSteps.value = 0
        _caloriesBurned.value = 0.0
    }
    
    fun resetTotalSteps() {
        _totalSteps.value = 0
        _sessionSteps.value = 0
        _caloriesBurned.value = 0.0
    }
    
    // No pull-based API; game collects from stepEvents
}

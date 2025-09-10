package com.malabarmatrix.slithersync.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malabarmatrix.slithersync.data.SensorRepository
import com.malabarmatrix.slithersync.data.StepRepository
import com.malabarmatrix.slithersync.domain.GameState
import com.malabarmatrix.slithersync.domain.SnakeEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(
    app: Application,
    private val stepRepo: StepRepository,
    private val sensorRepo: SensorRepository,
) : AndroidViewModel(app) {

    private var engine: SnakeEngine? = null

    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state

    private val _stepsToday = MutableStateFlow(0)
    val stepsToday: StateFlow<Int> = _stepsToday

    var stepPixels: Float = 10f
    private var heading: Float = 0f

    private var ticker: Job? = null

    fun attachCanvas(width: Int, height: Int) {
        if (engine == null) {
            engine = SnakeEngine(width, height, stepPixels = stepPixels)
            startLoops()
        }
    }

    private fun startLoops() {
        val e = engine ?: return
        viewModelScope.launch {
            sensorRepo.headingDegrees.collectLatest { deg ->
                heading = deg
            }
        }
        viewModelScope.launch {
            stepRepo.stepDeltaFlow.collectLatest { delta ->
                if (delta != 0) e.moveForwardBySteps(delta)
            }
        }
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                delay(16L)
                e.turnToDegrees(heading)
                e.tickBaseline(6f)
                _state.value = e.getState()
            }
        }
    }

    // Emulator helpers
    fun simulateSteps(delta: Int) {
        val e = engine ?: return
        if (delta != 0) e.moveForwardBySteps(delta)
    }

    fun setHeading(degrees: Float) { heading = degrees }
}

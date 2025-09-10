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

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _showStart = MutableStateFlow(true)
    val showStart: StateFlow<Boolean> = _showStart

    private val _showGameOver = MutableStateFlow(false)
    val showGameOver: StateFlow<Boolean> = _showGameOver

    private val _paused = MutableStateFlow(false)
    val paused: StateFlow<Boolean> = _paused

    var stepPixels: Float = 10f

    private var heading: Float = 0f
    private var touchActive: Boolean = false

    private var ticker: Job? = null

    private var lastCanvasWidth: Int = 0
    private var lastCanvasHeight: Int = 0

    fun attachCanvas(width: Int, height: Int) {
        lastCanvasWidth = width
        lastCanvasHeight = height
        // Do not create engine here; it will be created when game starts
        emitFrame()
        startLoopsIfNeeded()
    }

    private fun startLoopsIfNeeded() {
        if (ticker != null) return
        viewModelScope.launch {
            sensorRepo.headingDegrees.collectLatest { deg ->
                if (!touchActive) {
                    heading = deg
                }
            }
        }
        viewModelScope.launch {
            stepRepo.stepDeltaFlow.collectLatest { delta ->
                engine?.let { e -> if (!_paused.value && delta != 0) e.moveForwardBySteps(delta) }
            }
        }
        ticker = viewModelScope.launch {
            while (true) {
                delay(16L)
                val e = engine ?: continue
                if (_paused.value) continue
                e.turnToDegrees(heading)
                e.tickBaseline(6f)
                val s = e.getState()
                _state.value = s
                _score.value = s.score
                if (s.isGameOver) {
                    _showGameOver.value = true
                    _paused.value = true
                }
            }
        }
    }

    private fun emitFrame() {
        engine?.let {
            val s = it.getState()
            _state.value = s
            _score.value = s.score
        }
    }

    fun startGame() {
        if (lastCanvasWidth > 0 && lastCanvasHeight > 0) {
            engine = SnakeEngine(lastCanvasWidth, lastCanvasHeight, stepPixels = stepPixels)
            heading = 0f
            touchActive = false
            _score.value = 0
            _paused.value = false
            _showStart.value = false
            _showGameOver.value = false
            emitFrame()
        }
    }

    fun restart(width: Int = lastCanvasWidth, height: Int = lastCanvasHeight) {
        // Full reset: clear engine/state and show Start dialog
        engine = null
        heading = 0f
        touchActive = false
        _score.value = 0
        _paused.value = false
        _showGameOver.value = false
        _showStart.value = true
        _state.value = null
        // Canvas size persists in lastCanvasWidth/lastCanvasHeight
    }

    fun togglePause() { _paused.value = !_paused.value }

    // Emulator/helpers
    fun simulateSteps(delta: Int) {
        val e = engine ?: return
        if (!_paused.value && delta != 0) e.moveForwardBySteps(delta)
        emitFrame()
    }

    fun setHeading(degrees: Float) { heading = degrees }

    fun setTouchActive(active: Boolean) { touchActive = active }

    fun setTouchHeading(degrees: Float) {
        touchActive = true
        heading = degrees
    }
}

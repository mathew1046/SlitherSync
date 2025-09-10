package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import com.malabarmatrix.slithersync.domain.GameState
import com.malabarmatrix.slithersync.domain.SnakeEngine
import kotlinx.coroutines.delay

@Composable
fun GameScreen() {
    val stepPx = remember { mutableFloatStateOf(10f) }
    val heading = remember { mutableFloatStateOf(0f) }
    val stepsDelta = remember { mutableIntStateOf(0) }
    val state = remember { mutableStateOf<GameState?>(null) }

    // Canvas depends on size; create engine after we know size
    val view = LocalView.current
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        var engine: SnakeEngine? = remember { null }
        var size: IntSize? = remember { null }

        SnakeLayout(onSize = { s ->
            size = s
            if (engine == null) {
                engine = SnakeEngine(s.width, s.height, stepPixels = stepPx.floatValue)
            }
        }) { canvasSize ->
            size = canvasSize
            state.value?.let { SnakeCanvas(it) }
        }

        LaunchedEffect(size, stepPx.floatValue) {
            size?.let { s ->
                if (engine == null) engine = SnakeEngine(s.width, s.height, stepPixels = stepPx.floatValue)
            }
        }

        LaunchedEffect(engine) {
            while (true) {
                delay(16L) // ~60fps
                val e = engine ?: continue
                // baseline drift
                e.tickBaseline(6f * 1f)
                // apply simulated steps if any (currently zero default)
                val d = stepsDelta.intValue
                if (d != 0) {
                    e.moveForwardBySteps(d)
                    stepsDelta.intValue = 0
                }
                e.turnToDegrees(heading.floatValue)
                state.value = e.getState()
            }
        }

        // Minimal overlay for now
        state.value?.let { st ->
            DebugOverlay(rawStepsDelta = stepsDelta.intValue, headingDeg = heading.floatValue, stepPx = stepPx.floatValue)
        }
    }
}

@Composable
private fun SnakeLayout(onSize: (IntSize) -> Unit, content: @Composable (IntSize) -> Unit) {
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth
        val heightPx = constraints.maxHeight
        val size = IntSize(widthPx, heightPx)
        LaunchedEffect(widthPx, heightPx) { onSize(size) }
        content(size)
    }
}

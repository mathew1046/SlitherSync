package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.malabarmatrix.slithersync.domain.GameState
import kotlin.math.atan2

@Composable
fun GameView(state: GameState?, onSize: (Int, Int) -> Unit, onDragHeading: (Float) -> Unit) {
    var w = remember { mutableFloatStateOf(0f) }
    var h = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                w.floatValue = size.width.toFloat()
                h.floatValue = size.height.toFloat()
                onSize(size.width, size.height)
            }
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = { }, onDragEnd = { }, onDragCancel = { }) { change, _ ->
                    val cx = w.floatValue / 2f
                    val cy = h.floatValue / 2f
                    val dx = change.position.x - cx
                    val dy = change.position.y - cy
                    var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (deg < 0) deg += 360f
                    onDragHeading(deg)
                }
            }
    ) {
        state?.let { SnakeCanvas(it) }
    }
}

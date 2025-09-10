package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.malabarmatrix.slithersync.domain.GameState

@Composable
fun SnakeCanvas(state: GameState, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // border
        drawRect(color = Color(0xFF23303A), style = Stroke(width = 6f))

        // subtle grid
        val step = 64f
        var x = 0f
        while (x <= size.width) {
            drawLine(color = Color(0x2210FFFFFF), start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(color = Color(0x2210FFFFFF), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            y += step
        }

        // food
        state.food?.let { f ->
            drawCircle(
                color = Color(0xFFE91E63),
                radius = 10f,
                center = Offset(f.x, f.y)
            )
        }
        // snake
        val segments = state.segments
        if (segments.size >= 2) {
            for (i in 0 until segments.size - 1) {
                val a = segments[i].position
                val b = segments[i + 1].position
                drawLine(
                    color = Color(0xFF4CAF50),
                    start = Offset(a.x, a.y),
                    end = Offset(b.x, b.y),
                    strokeWidth = 14f,
                    cap = StrokeCap.Round
                )
            }
        } else if (segments.isNotEmpty()) {
            val p = segments.first().position
            drawCircle(Color(0xFF4CAF50), radius = 7f, center = Offset(p.x, p.y))
        }
    }
}

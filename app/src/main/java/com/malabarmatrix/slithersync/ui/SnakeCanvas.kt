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
        // Draw food
        state.food?.let { f ->
            drawCircle(
                color = Color(0xFFE91E63),
                radius = 10f,
                center = Offset(f.x, f.y)
            )
        }
        // Draw snake as a stroked path (segments as points)
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

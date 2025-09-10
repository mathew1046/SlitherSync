package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.malabarmatrix.slithersync.domain.GameState

@Composable
fun SnakeCanvas(state: GameState, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val margin = 10f
        // double border inside margin
        drawRect(
            color = Color(0xFF2E3D47),
            topLeft = Offset(margin, margin),
            size = androidx.compose.ui.geometry.Size(size.width - 2 * margin, size.height - 2 * margin),
            style = Stroke(width = 8f)
        )
        drawRect(
            color = Color(0xFF00BCD4),
            topLeft = Offset(margin, margin),
            size = androidx.compose.ui.geometry.Size(size.width - 2 * margin, size.height - 2 * margin),
            style = Stroke(width = 2f)
        )

        // subtle grid
        val step = 64f
        var x = margin
        while (x <= size.width - margin) {
            drawLine(color = Color(0x2210FFFFFF), start = Offset(x, margin), end = Offset(x, size.height - margin), strokeWidth = 1f)
            x += step
        }
        var y = margin
        while (y <= size.height - margin) {
            drawLine(color = Color(0x2210FFFFFF), start = Offset(margin, y), end = Offset(size.width - margin, y), strokeWidth = 1f)
            y += step
        }

        // food
        state.food?.let { f ->
            drawCircle(
                color = Color(0xFFFF4081),
                radius = 12f,
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
                    color = Color(0xFF76FF03),
                    start = Offset(a.x, a.y),
                    end = Offset(b.x, b.y),
                    strokeWidth = 16f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        } else if (segments.isNotEmpty()) {
            val p = segments.first().position
            drawCircle(Color(0xFF76FF03), radius = 8f, center = Offset(p.x, p.y))
        }
    }
}

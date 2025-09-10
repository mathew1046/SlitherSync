package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(score: Int, stepsToday: Int, paused: Boolean, onTogglePause: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Score: $score", style = MaterialTheme.typography.titleMedium)
        Text(text = "Steps: $stepsToday", style = MaterialTheme.typography.titleMedium)
        Button(onClick = onTogglePause) { Text(if (paused) "Resume" else "Pause") }
    }
}

@Composable
fun SettingsPanel(stepPx: Float, onStepPxChange: (Float) -> Unit, headingAlpha: Float, onHeadingAlphaChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Step -> px multiplier: ${stepPx.toInt()}")
        Slider(value = stepPx, onValueChange = onStepPxChange, valueRange = 4f..24f)
        Spacer(Modifier.height(8.dp))
        Text("Heading smoothing alpha: ${"%.2f".format(headingAlpha)}")
        Slider(value = headingAlpha, onValueChange = onHeadingAlphaChange, valueRange = 0.02f..0.30f)
    }
}

@Composable
fun DebugOverlay(rawStepsDelta: Int, headingDeg: Float, stepPx: Float) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text("Δsteps: $rawStepsDelta")
        Text("Heading: ${headingDeg.toInt()}°")
        Text("px/step: ${stepPx.toInt()}")
    }
}

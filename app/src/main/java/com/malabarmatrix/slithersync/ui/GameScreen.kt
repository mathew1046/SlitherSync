package com.malabarmatrix.slithersync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.malabarmatrix.slithersync.ui.state.GameViewModel
import kotlin.math.atan2

@Composable
fun GameScreen(vm: GameViewModel) {
    val state by vm.state.collectAsState(initial = null)
    val score by vm.score.collectAsState(initial = 0)
    val showStart by vm.showStart.collectAsState(initial = true)
    val showGameOver by vm.showGameOver.collectAsState(initial = false)
    val paused by vm.paused.collectAsState(initial = false)
    
    // Step tracking data
    val sessionSteps by vm.sessionSteps.collectAsState(initial = 0)
    val caloriesBurned by vm.caloriesBurned.collectAsState(initial = 0.0)
    val isStepDetectionActive by vm.isStepDetectionActive.collectAsState(initial = false)

    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    if (showStart) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("SlitherSync") },
            text = { 
                Column {
                    Text("Welcome to SlitherSync!")
                    Text("")
                    Text("• Snake moves forward with each step you take")
                    Text("• Drag to steer the snake direction")
                    Text("• Snake stays at rest when you stop walking")
                    Text("• Use +5/+20 buttons for testing")
                    Text("")
                    Text("Make sure to grant activity recognition permission!")
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.startGame() }) { Text("Start Walking!") }
            }
        )
    }

    if (showGameOver) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Game Over") },
            text = { Text("Score: $score") },
            confirmButton = {
                TextButton(onClick = {
                    vm.restart(canvasWidth.toInt(), canvasHeight.toInt())
                    vm.startGame()
                }) { Text("Restart") }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                Modifier.fillMaxWidth().background(Color(0xFF0E1115)).padding(12.dp)
            ) {
                // First row: App name, Score, Controls
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: App name
                    Text(
                        "SlitherSync",
                        color = Color(0xFF8AB4F8),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    // Center: Live score
                    Text(
                        "Score: $score",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    // Right: Pause/Resume + Restart
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { vm.togglePause() }) { Text(if (paused) "Resume" else "Pause") }
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.padding(horizontal = 4.dp))
                        Button(onClick = { vm.restart(canvasWidth.toInt(), canvasHeight.toInt()); vm.startGame() }) { Text("Restart") }
                    }
                }
                
                // Second row: Step tracking
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Steps: $sessionSteps",
                        color = Color(0xFF76FF03),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Calories: ${String.format("%.1f", caloriesBurned)}",
                        color = Color(0xFFFF4081),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        if (isStepDetectionActive) "Sensors: Active" else "Sensors: Inactive",
                        color = if (isStepDetectionActive) Color(0xFF4CAF50) else Color(0xFFFF5722),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF101418))
                .onSizeChanged { size ->
                    canvasWidth = size.width.toFloat()
                    canvasHeight = size.height.toFloat()
                    if (size.width > 0 && size.height > 0) {
                        vm.attachCanvas(size.width, size.height)
                    }
                }
                .pointerInput(Unit) {
                    var totalDx = 0f
                    var totalDy = 0f
                    val swipeThresholdPx = 48f
                    detectDragGestures(
                        onDragStart = {
                            totalDx = 0f
                            totalDy = 0f
                        },
                        onDragEnd = {
                            val absDx = kotlin.math.abs(totalDx)
                            val absDy = kotlin.math.abs(totalDy)
                            if (absDx < swipeThresholdPx && absDy < swipeThresholdPx) return@detectDragGestures
                            val headingDeg = if (absDx > absDy) {
                                if (totalDx > 0) 0f else 180f // right or left
                            } else {
                                if (totalDy > 0) 90f else 270f // down or up
                            }
                            vm.setTouchHeading(headingDeg)
                        },
                        onDragCancel = { },
                    ) { change, dragAmount ->
                        totalDx += dragAmount.x
                        totalDy += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            state?.let { SnakeCanvas(it) }
            if (state == null) {
                Text(
                    text = "Initializing...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp).alpha(0.7f)
                )
            }
        }
    }
}

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

    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    if (showStart) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("SlitherSync") },
            text = { Text("Tap Start to play. Use +5/+20 or steps; drag to steer.") },
            confirmButton = {
                TextButton(onClick = { vm.startGame() }) { Text("Start") }
            }
        )
    }

    if (showGameOver) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Game Over") },
            text = { Text("Score: $score") },
            confirmButton = {
                TextButton(onClick = { vm.restart(canvasWidth.toInt(), canvasHeight.toInt()) }) { Text("Restart") }
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF0E1115)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Score: $score", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { vm.togglePause() }) { Text(if (paused) "Resume" else "Pause") }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { vm.simulateSteps(5) }) { Text("+5") }
                        Button(onClick = { vm.simulateSteps(20) }) { Text("+20") }
                    }
                }
            )
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
                    detectDragGestures(
                        onDragStart = { vm.setTouchActive(true) },
                        onDragEnd = { vm.setTouchActive(false) },
                        onDragCancel = { vm.setTouchActive(false) }
                    ) { change, _ ->
                        val centerX = canvasWidth / 2f
                        val centerY = canvasHeight / 2f
                        val dx = change.position.x - centerX
                        val dy = change.position.y - centerY
                        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (deg < 0) deg += 360f
                        vm.setTouchHeading(deg)
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

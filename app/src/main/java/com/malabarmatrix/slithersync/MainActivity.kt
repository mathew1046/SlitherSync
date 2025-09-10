package com.malabarmatrix.slithersync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.malabarmatrix.slithersync.data.AndroidSensorRepository
import com.malabarmatrix.slithersync.data.SimulatedStepRepository
import com.malabarmatrix.slithersync.ui.GameScreen
import com.malabarmatrix.slithersync.ui.state.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val vm: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val steps = SimulatedStepRepository()
                            val sensors = AndroidSensorRepository(applicationContext)
                            @Suppress("UNCHECKED_CAST")
                            return GameViewModel(application, steps, sensors) as T
                        }
                    })
                    GameScreenWithVm(vm)
                }
            }
        }
    }
}

@Composable
private fun GameScreenWithVm(vm: GameViewModel) {
    // Provide simple emulator controls overlay for now
    var heading by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(heading) { vm.setHeading(heading) }

    Box(Modifier.fillMaxSize()) {
        GameScreenWithEngine(vm)
        // rudimentary controls; real UI is in GameScreen later
        Button(onClick = { vm.simulateSteps(5) }) { Text("+5 steps") }
    }
}

@Composable
private fun GameScreenWithEngine(vm: GameViewModel) {
    GameScreen()
}

package com.malabarmatrix.slithersync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
                    GameScreen(vm)
                }
            }
        }
    }
}

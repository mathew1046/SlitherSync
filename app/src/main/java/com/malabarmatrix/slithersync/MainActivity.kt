package com.malabarmatrix.slithersync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.malabarmatrix.slithersync.data.AndroidSensorRepository
import com.malabarmatrix.slithersync.data.SimulatedStepRepository
import com.malabarmatrix.slithersync.ui.GameScreen
import com.malabarmatrix.slithersync.ui.state.GameViewModel
import com.malabarmatrix.slithersync.ui.state.StepViewModel
import com.malabarmatrix.slithersync.util.Permissions

class MainActivity : ComponentActivity() {
    
    private val activityRecognitionPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled in the UI
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request activity recognition permission if needed
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val vm: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val steps = SimulatedStepRepository()
                            val sensors = AndroidSensorRepository(applicationContext)
                            val stepViewModel = StepViewModel(application)
                            @Suppress("UNCHECKED_CAST")
                            return GameViewModel(application, steps, sensors, stepViewModel) as T
                        }
                    })
                    GameScreen(vm)
                }
            }
        }
    }
}

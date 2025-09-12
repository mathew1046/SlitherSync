package com.malabarmatrix.slithersync

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.malabarmatrix.slithersync.api.GoogleFitManager
import com.malabarmatrix.slithersync.api.OpenWeatherMapManager

class MainActivity : ComponentActivity() {
    
    private val activityRecognitionPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled in the UI
    }

    private lateinit var googleFitManager: GoogleFitManager
    private lateinit var gameViewModel: GameViewModel

    private val googleFitSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Permissions granted, fetch data
            gameViewModel.loadInitialData()
        } else {
            // Permissions denied
            Log.w("MainActivity", "Google Fit permissions denied.")
        }
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
                            googleFitManager = GoogleFitManager(applicationContext)
                            val openWeatherMapManager = OpenWeatherMapManager(applicationContext)
                            @Suppress("UNCHECKED_CAST")
                            gameViewModel = GameViewModel(application, steps, sensors, stepViewModel, googleFitManager, openWeatherMapManager)
                            return gameViewModel as T
                        }
                    })

                    if (!googleFitManager.isPermissionGranted()) {
                        googleFitManager.requestPermissions(this, googleFitSignInLauncher)
                    } else {
                        vm.loadInitialData()
                    }
                    GameScreen(vm)
                }
            }
        }
    }
}

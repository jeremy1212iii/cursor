package com.situpcounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.pose.Pose
import com.situpcounter.camera.CameraManager
import com.situpcounter.camera.PoseAnalyzer
import com.situpcounter.databinding.ActivityMainBinding
import com.situpcounter.viewmodel.SitUpViewModel
import kotlinx.coroutines.launch

/**
 * Main Activity for Sit-Up Counter App
 * Implements camera-based pose detection and counting
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SitUpViewModel
    private lateinit var cameraManager: CameraManager
    private lateinit var poseAnalyzer: PoseAnalyzer
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeCamera()
        } else {
            Toast.makeText(
                this,
                getString(R.string.camera_permission_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[SitUpViewModel::class.java]
        
        // Setup UI observers
        setupObservers()
        
        // Setup button listeners
        setupButtonListeners()
        
        // Check and request camera permission
        checkCameraPermission()
    }
    
    private fun setupObservers() {
        viewModel.count.observe(this) { count ->
            binding.countTextView.text = getString(R.string.count, count)
        }
        
        viewModel.statusText.observe(this) { status ->
            binding.statusTextView.text = status
        }
        
        viewModel.isCounting.observe(this) { isCounting ->
            binding.startButton.isEnabled = !isCounting
            binding.pauseButton.isEnabled = isCounting
        }
    }
    
    private fun setupButtonListeners() {
        binding.startButton.setOnClickListener {
            viewModel.startCounting()
            poseAnalyzer.resumeCounter()
        }
        
        binding.pauseButton.setOnClickListener {
            viewModel.pauseCounting()
            poseAnalyzer.pauseCounter()
        }
        
        binding.resetButton.setOnClickListener {
            viewModel.reset()
            poseAnalyzer.resetCounter()
        }
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun initializeCamera() {
        // Initialize pose analyzer
        poseAnalyzer = PoseAnalyzer { pose, count ->
            lifecycleScope.launch {
                // Update count in ViewModel
                viewModel.updateCount(count)
                
                // Update pose overlay
                binding.poseOverlay.updatePose(pose)
            }
        }
        
        // Initialize camera manager
        cameraManager = CameraManager(
            context = this,
            previewView = binding.cameraPreview,
            lifecycleOwner = this,
            poseAnalyzer = poseAnalyzer
        )
        
        // Start camera
        cameraManager.startCamera()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::cameraManager.isInitialized) {
            cameraManager.stopCamera()
            cameraManager.shutdown()
        }
    }
}

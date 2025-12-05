package com.situpcounter.camera

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.situpcounter.utils.SitUpCounter

/**
 * Image Analyzer for Pose Detection using ML Kit
 */
class PoseAnalyzer(
    private val onPoseDetected: (Pose?, Int) -> Unit
) : ImageAnalysis.Analyzer {
    
    private val poseDetector = PoseDetection.getClient(
        AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
    )
    
    private val sitUpCounter = SitUpCounter()
    
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            
            poseDetector.process(image)
                .addOnSuccessListener { pose ->
                    // Extract landmarks
                    val landmarks = pose.allPoseLandmarks.associateBy { it.landmarkType }
                    
                    // Process sit-up counting
                    val count = sitUpCounter.processPose(landmarks)
                    
                    // Notify listener
                    onPoseDetected(pose, count)
                }
                .addOnFailureListener { e ->
                    onPoseDetected(null, sitUpCounter.getCount())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    
    fun resetCounter() {
        sitUpCounter.reset()
    }
    
    fun pauseCounter() {
        sitUpCounter.pause()
    }
    
    fun resumeCounter() {
        sitUpCounter.resume()
    }
    
    fun getCount(): Int = sitUpCounter.getCount()
}

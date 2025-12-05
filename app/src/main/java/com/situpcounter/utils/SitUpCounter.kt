package com.situpcounter.utils

import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Sit-Up Counter Logic
 * Uses state machine to track sit-up movements
 */
class SitUpCounter {
    
    enum class State {
        IDLE,           // Initial state
        DOWN,           // Person is lying down
        GOING_UP,       // Person is moving up (sit-up in progress)
        UP,             // Person is sitting up
        GOING_DOWN      // Person is returning to lying position
    }
    
    private var currentState = State.IDLE
    private var count = 0
    private var isPaused = false
    
    // Thresholds for detection (normalized coordinates 0-1)
    private val SHOULDER_HIP_THRESHOLD = 0.15f  // Minimum vertical distance between shoulder and hip
    private val MOVEMENT_THRESHOLD = 0.05f      // Minimum movement to trigger state change
    
    /**
     * Process pose landmarks and update counter
     * @param landmarks Map of pose landmarks
     * @return Updated count
     */
    fun processPose(landmarks: Map<Int, PoseLandmark>): Int {
        if (isPaused) return count
        
        // Get key landmarks
        val leftShoulder = landmarks[PoseLandmark.LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmark.RIGHT_SHOULDER]
        val leftHip = landmarks[PoseLandmark.LEFT_HIP]
        val rightHip = landmarks[PoseLandmark.RIGHT_HIP]
        
        // Validate that we have all required landmarks
        if (leftShoulder == null || rightShoulder == null || 
            leftHip == null || rightHip == null) {
            return count
        }
        
        // Calculate average positions
        val avgShoulderY = (leftShoulder.position.y + rightShoulder.position.y) / 2f
        val avgHipY = (leftHip.position.y + rightHip.position.y) / 2f
        
        // Calculate vertical distance (normalized)
        val verticalDistance = avgHipY - avgShoulderY
        
        // State machine logic
        when (currentState) {
            State.IDLE -> {
                // Initialize: determine if person is lying down or sitting
                if (verticalDistance > SHOULDER_HIP_THRESHOLD) {
                    currentState = State.DOWN
                } else {
                    currentState = State.UP
                }
            }
            
            State.DOWN -> {
                // Check if person is starting to sit up
                if (verticalDistance < SHOULDER_HIP_THRESHOLD - MOVEMENT_THRESHOLD) {
                    currentState = State.GOING_UP
                }
            }
            
            State.GOING_UP -> {
                // Check if person has reached sitting position
                if (verticalDistance < SHOULDER_HIP_THRESHOLD) {
                    currentState = State.UP
                }
            }
            
            State.UP -> {
                // Check if person is returning to lying position
                if (verticalDistance > SHOULDER_HIP_THRESHOLD + MOVEMENT_THRESHOLD) {
                    currentState = State.GOING_DOWN
                }
            }
            
            State.GOING_DOWN -> {
                // Check if person has returned to lying position
                if (verticalDistance > SHOULDER_HIP_THRESHOLD) {
                    currentState = State.DOWN
                    count++ // Increment counter when complete cycle detected
                }
            }
        }
        
        return count
    }
    
    fun getCount(): Int = count
    
    fun reset() {
        count = 0
        currentState = State.IDLE
        isPaused = false
    }
    
    fun pause() {
        isPaused = true
    }
    
    fun resume() {
        isPaused = false
    }
    
    fun getState(): State = currentState
}

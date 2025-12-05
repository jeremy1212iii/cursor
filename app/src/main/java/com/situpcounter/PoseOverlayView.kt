package com.situpcounter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Custom View to overlay pose landmarks on camera preview
 */
class PoseOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var currentPose: Pose? = null
    
    // Paint objects for drawing
    private val landmarkPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 8f
    }
    
    private val connectionPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    // Pose connections (key points to draw lines between)
    private val connections = listOf(
        Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
        Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
        Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
        Pair(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP)
    )
    
    fun updatePose(pose: Pose?) {
        currentPose = pose
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        currentPose?.let { pose ->
            // Draw connections
            connections.forEach { (startType, endType) ->
                val startLandmark = pose.getPoseLandmark(startType)
                val endLandmark = pose.getPoseLandmark(endType)
                
                if (startLandmark != null && endLandmark != null) {
                    val start = scalePoint(startLandmark.position)
                    val end = scalePoint(endLandmark.position)
                    canvas.drawLine(start.x, start.y, end.x, end.y, connectionPaint)
                }
            }
            
            // Draw key landmarks for sit-up detection
            val keyLandmarks = listOf(
                PoseLandmark.LEFT_SHOULDER,
                PoseLandmark.RIGHT_SHOULDER,
                PoseLandmark.LEFT_HIP,
                PoseLandmark.RIGHT_HIP
            )
            
            keyLandmarks.forEach { landmarkType ->
                val landmark = pose.getPoseLandmark(landmarkType)
                landmark?.let {
                    val point = scalePoint(it.position)
                    canvas.drawCircle(point.x, point.y, 12f, landmarkPaint)
                }
            }
        }
    }
    
    /**
     * Scale point from normalized coordinates (0-1) to view coordinates
     */
    private fun scalePoint(point: PointF): PointF {
        return PointF(
            point.x * width,
            point.y * height
        )
    }
}

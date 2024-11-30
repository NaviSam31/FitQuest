package com.example.waterwisee

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomPieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var completedTime: Long = 0L // Time completed (in milliseconds)
    private var totalTime: Long = 1L // Total time (in milliseconds) - avoid division by zero

    private val completedColor = Color.GREEN
    private val remainingColor = Color.RED

    // Method to set progress based on time
    fun setTaskProgress(completed: Long, total: Long) {
        completedTime = completed
        totalTime = if (total > 0) total else 1L // Prevent division by zero
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate center and radius dynamically based on view size
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = (Math.min(width, height) / 2) - 50f // Padding from edges
        val centerX = width / 2
        val centerY = height / 2

        // Calculate sweep angles based on time
        val sweepAngleCompleted = (completedTime.toFloat() / totalTime) * 360f
        val sweepAngleRemaining = 360f - sweepAngleCompleted

        // Draw completed tasks slice
        paint.color = completedColor
        canvas.drawArc(
            centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            270f, sweepAngleCompleted, true, paint
        )

        // Draw remaining tasks slice
        paint.color = remainingColor
        canvas.drawArc(
            centerX - radius, centerY - radius, centerX + radius, centerY + radius,
            270f + sweepAngleCompleted, sweepAngleRemaining, true, paint
        )
    }
}

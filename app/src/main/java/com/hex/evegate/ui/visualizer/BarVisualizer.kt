package com.hex.evegate.ui.visualizer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

class BarVisualizer : BaseVisualizer {

    private var density = 50f
    private var gap: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context,
                attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun init() {
        this.density = 50f
        this.gap = 4
        paint.style = Paint.Style.FILL
    }

    fun setDensity(density: Float) {
        this.density = density
        if (density > 256) {
            this.density = 256f
        } else if (density < 10) {
            this.density = 10f
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (bytes != null) {
            val barWidth = width / density
            val div = bytes!!.size / density
            paint.strokeWidth = barWidth - gap

            var i = 0
            while (i < density) {
                val bytePosition = Math.ceil((i * div).toDouble()).toInt()
                val top = height + (Math.abs(bytes!![bytePosition].toInt()) + 128).toByte() * height / 128
                val barX = i * barWidth + barWidth / 2
                canvas.drawLine(barX, height.toFloat(), barX, top.toFloat(), paint)
                i++
            }
            super.onDraw(canvas)
        }
    }
}

package com.hex.evegate.ui.visualizer

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.view.View

abstract class BaseVisualizer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    protected var bytes: ByteArray? = null
    protected lateinit var paint: Paint
    var visualizer: Visualizer? = null
        protected set
    private var color = Color.BLUE

    init {
        init(attrs)
        init()
    }

    private fun init(attributeSet: AttributeSet?) {
        paint = Paint()
    }

    fun setColor(color: Int) {
        this.color = color
        this.paint.color = this.color
    }

    @Deprecated("", ReplaceWith("setPlayer(mediaPlayer.audioSessionId)"))
    fun setPlayer(mediaPlayer: MediaPlayer) {
        setPlayer(mediaPlayer.audioSessionId)
    }

    fun setPlayer(audioSessionId: Int) {
        if (visualizer != null) {
            release()
        }

        visualizer = Visualizer(audioSessionId)
        visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[1]

        visualizer!!.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(visualizer: Visualizer, bytes: ByteArray,
                                               samplingRate: Int) {
                this@BaseVisualizer.bytes = bytes
                invalidate()
            }

            override fun onFftDataCapture(visualizer: Visualizer, bytes: ByteArray,
                                          samplingRate: Int) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false)

        visualizer!!.enabled = true
    }

    fun release() {
        visualizer!!.release()
    }

    protected abstract fun init()
}

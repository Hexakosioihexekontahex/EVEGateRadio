package com.hex.evegate.radio

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hex.evegate.AppEx
import org.greenrobot.eventbus.EventBus

class RadioManager private constructor(private val context: Context) {

    private var serviceBound: Boolean = false

    val isPlaying: Boolean
        get() = service != null && service!!.isPlaying

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            service = (binder as RadioService.LocalBinder).service
            serviceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {

            serviceBound = false
        }
    }

    init {
        serviceBound = false
    }

    fun playOrPause(streamUrl: String) {
        if (service != null)
            service!!.playOrPause(streamUrl)
    }

    fun bind() {
        val intent = Intent(context, RadioService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (service != null)
            EventBus.getDefault().post(service!!.status)
    }

    fun unbind() {
        context.unbindService(serviceConnection)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: RadioManager? = null

        var service: RadioService? = null
            private set

        fun getInstance(): RadioManager {
            if (instance == null)
                instance = RadioManager(AppEx.instance!!)
            return instance!!
        }
    }
}

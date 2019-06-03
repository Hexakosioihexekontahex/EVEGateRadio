package com.hex.evegate

import android.app.Application
import android.content.Context

class AppEx : Application() {

    companion object {
        @JvmStatic
        var instance: AppEx? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    var shpHQ: Boolean
        get() {
            return getSharedPreferences(instance?.packageName, Context.MODE_PRIVATE)
                    ?.getBoolean("HighQuality", true)!!
        }
        set(value) {
            val sPref = getSharedPreferences(instance?.packageName, Context.MODE_PRIVATE)
            val ed = sPref?.edit()
            ed?.putBoolean("HighQuality", value)
            ed?.apply()
        }
}
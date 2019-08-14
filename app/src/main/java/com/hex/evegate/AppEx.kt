package com.hex.evegate

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class AppEx : Application() {

    companion object {
        @JvmStatic
        var instance: AppEx? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fabric.with(this, Crashlytics())
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
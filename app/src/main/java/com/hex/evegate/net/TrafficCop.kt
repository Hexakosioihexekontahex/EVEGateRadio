package com.hex.evegate.net

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.TrafficStats
import android.os.Bundle

import java.util.ArrayList
import java.util.Arrays

/**
 * A simple way to get detect data usage over a threshold.
 */
class TrafficCop private constructor(context: Context, private val id: String, private val dataUsageAlertListeners: List<DataUsageAlertListener>, private val downloadWarningThreshold: Threshold, private val uploadWarningThreshold: Threshold, private val dataUsageStatsProvider: DataUsageStatsProvider) {

    private var startTime: Long = -1
    private var bytesTransmitted: Long = -1
    private var bytesReceived: Long = -1
    private val prefs: SharedPreferences
    private var application: Application? = null
    private var activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks? = null
    private var isDestroyed: Boolean = false

    init {
        this.prefs = context.getSharedPreferences(SHARED_PREFS_NAME + id, Context.MODE_PRIVATE)
    }

    /**
     * Starts measuring data usage. If you are using
     * [TrafficCop.register] then you don't need to call this.
     */
    fun startMeasuring() {
        if (isDestroyed) {
            throw IllegalStateException("The TrafficCop has been destroyed.")
        }

        startTime = dataUsageStatsProvider.nanoTime
        bytesTransmitted = dataUsageStatsProvider.bytesTransmitted
        bytesReceived = dataUsageStatsProvider.bytesReceived
    }

    /**
     * Stops measuring data usage and possible triggers the listeners if a threshold is met. If you
     * are using [TrafficCop.register] then you don't need to call
     * this.
     */
    fun stopMeasuring() {
        if (isDestroyed) {
            throw IllegalStateException("The TrafficCop has been destroyed.")
        }

        if (startTime == -1L) {
            return
        }

        val elapsedTime = ((dataUsageStatsProvider.nanoTime - startTime) / 1000000000).toInt()
        var receivedDelta = dataUsageStatsProvider.bytesReceived - bytesReceived
        var transmittedDelta = dataUsageStatsProvider.bytesTransmitted - bytesTransmitted

        val elapsedTimeDownload = elapsedTime + prefs.getInt(PREFS_ELAPSED_SECONDS_DOWNLOAD, 0)
        val elapsedTimeUpload = elapsedTime + prefs.getInt(PREFS_ELAPSED_SECONDS_UPLOAD, 0)
        receivedDelta += prefs.getLong(PREFS_DOWNLOAD_BYTES, 0)
        transmittedDelta += prefs.getLong(PREFS_UPLOAD_BYTES, 0)

        val downloadUsage = DataUsage(DataUsage.Type.DOWNLOAD, receivedDelta, elapsedTimeDownload)
        val uploadUsage = DataUsage(DataUsage.Type.UPLOAD, transmittedDelta, elapsedTimeUpload)

        var hasWarnedDownload = false
        if (downloadWarningThreshold.hasReached(downloadUsage)) {
            hasWarnedDownload = true
            for (adapter in dataUsageAlertListeners) {
                adapter.alertThreshold(downloadWarningThreshold, downloadUsage)
            }
        }

        var hasWarnedUpload = false
        if (uploadWarningThreshold.hasReached(uploadUsage)) {
            hasWarnedUpload = true
            for (adapter in dataUsageAlertListeners) {
                adapter.alertThreshold(uploadWarningThreshold, uploadUsage)
            }
        }

        val editor = prefs.edit()

        if (hasWarnedDownload) {
            editor.remove(PREFS_DOWNLOAD_BYTES)
            editor.remove(PREFS_ELAPSED_SECONDS_DOWNLOAD)
        } else {
            editor.putInt(PREFS_ELAPSED_SECONDS_DOWNLOAD, elapsedTimeDownload)
            editor.putLong(PREFS_DOWNLOAD_BYTES, receivedDelta)
        }

        if (hasWarnedUpload) {
            editor.remove(PREFS_UPLOAD_BYTES)
            editor.remove(PREFS_ELAPSED_SECONDS_UPLOAD)
        } else {
            editor.putLong(PREFS_UPLOAD_BYTES, transmittedDelta)
            editor.putInt(PREFS_ELAPSED_SECONDS_UPLOAD, elapsedTimeUpload)
        }

        editor.apply()
    }

    /**
     * Register the TrafficCop to the activity lifecycle. If you call this, you don't need to call
     * [.stopMeasuring]/[.startMeasuring].
     *
     * @param application the application context.
     */
    fun register(application: Application) {
        if (isDestroyed) {
            throw IllegalStateException("The TrafficCop has been destroyed.")
        }

        this.application = application
        activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) { }
            override fun onActivityStarted(activity: Activity) { }
            override fun onActivityResumed(activity: Activity) { startMeasuring() }
            override fun onActivityPaused(activity: Activity) { stopMeasuring() }
            override fun onActivityStopped(activity: Activity) { }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }
            override fun onActivityDestroyed(activity: Activity) { }
        }
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    /**
     * Unregister the TrafficCop from the activity lifecycle. You may call this after
     * [.register] if you no longer want to be notified.
     */
    fun unregister() {
        if (activityLifecycleCallbacks != null) {
            application!!.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
            activityLifecycleCallbacks = null
            application = null
        }
    }

    /**
     * Destroys the TrafficCop. You cannot call any other methods on this traffic cop after calling
     * this method, but you are now free to create another one with the same id.
     */
    fun destroy() {
        isDestroyed = true
        TRAFFIC_COP_IDS.remove(id)
        prefs.edit().clear().apply()
        unregister()
    }

    /**
     * Constructs a new TrafficCop.
     */
    class Builder {
        private val adapters = ArrayList<DataUsageAlertListener>()
        private var downloadWarningThreshold = Threshold.none()
        private var uploadWarningThreshold = Threshold.none()
        private var dataUsageStatsProvider: DataUsageStatsProvider? = null

        /**
         * Register one or more listeners that will be called when your app's data usage goes over a threshold.
         *
         * @param listeners the listeners to register
         * @return the builder for chaining
         */
        fun alert(vararg listeners: DataUsageAlertListener): Builder {
            this.adapters.addAll(Arrays.asList(*listeners))
            return this
        }

        /**
         * Register a collection of listeners that will be called when your app's data usage goes over a threshold.
         *
         * @param listeners the listeners to register
         * @return the builder for chaining
         */
        fun alert(listeners: Collection<DataUsageAlertListener>): Builder {
            this.adapters.addAll(listeners)
            return this
        }

        /**
         * Set the download threshold to be hit to notify the callback.
         *
         * @param threshold the threshold to hit
         * @return the builder for chaining
         */
        fun downloadWarningThreshold(threshold: Threshold?): Builder {
            if (threshold == null) {
                throw IllegalArgumentException("downloadWarningThreshold cannot be null")
            }
            downloadWarningThreshold = threshold
            return this
        }

        /**
         * Set the upload threshold to be hit to notify the callback.
         *
         * @param threshold the threshold to hit
         * @return the builder for caching
         */
        fun uploadWarningThreshold(threshold: Threshold?): Builder {
            if (threshold == null) {
                throw IllegalArgumentException("uploadWarningThreshold cannot be null")
            }
            uploadWarningThreshold = threshold
            return this
        }

        /**
         * Set the provider that collects the data usage stats. This does not need to be called by
         * default, but you may provide another implementation for more complex monitoring or for
         * testing.
         *
         * @param provider the provider
         * @return the builder for caching
         */
        fun dataUsageStatsProvider(provider: DataUsageStatsProvider): Builder {
            this.dataUsageStatsProvider = provider
            return this
        }

        /**
         * Construct the TrafficCop with the current configuration.
         *
         * @param context the context
         * @return the TrafficCop
         */
        fun create(id: String, context: Context): TrafficCop {
            if (TRAFFIC_COP_IDS.contains(id)) {
                throw IllegalArgumentException("A TrafficCop with id '$id' has already been created.")
            }
            TRAFFIC_COP_IDS.add(id)

            if (dataUsageStatsProvider == null) {
                dataUsageStatsProvider = DataUsageStatsProviderImpl(context.applicationInfo.uid)
            }
            return TrafficCop(context.applicationContext, id, adapters, downloadWarningThreshold,
                    uploadWarningThreshold, dataUsageStatsProvider!!)
        }

        /**
         * Construct the TrafficCop with the current configuration and register it to the activity
         * lifecycle.
         *
         * @param application the application context.
         * @return the TrafficCop
         * @see TrafficCop.register
         */
        fun register(id: String, application: Application): TrafficCop {
            val trafficCop = create(id, application)
            trafficCop.register(application)
            return trafficCop
        }
    }

    private class DataUsageStatsProviderImpl internal constructor(private val uid: Int) : DataUsageStatsProvider {

        override val nanoTime: Long
            get() = System.nanoTime()

        override val bytesTransmitted: Long
            get() = TrafficStats.getUidTxBytes(uid)

        override val bytesReceived: Long
            get() = TrafficStats.getUidRxBytes(uid)
    }

    companion object {
        private val TRAFFIC_COP_IDS = ArrayList<String>()
        private val SHARED_PREFS_NAME = TrafficCop::class.java.canonicalName!! + "_shared_prefs"
        private val PREFS_ELAPSED_SECONDS_DOWNLOAD = "elapsed_second_upload"
        private val PREFS_ELAPSED_SECONDS_UPLOAD = "elapsed_second_upload"
        private val PREFS_DOWNLOAD_BYTES = "download_bytes"
        private val PREFS_UPLOAD_BYTES = "upload_bytes"
    }
}

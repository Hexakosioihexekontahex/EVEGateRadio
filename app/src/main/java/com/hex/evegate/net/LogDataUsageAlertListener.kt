package com.hex.evegate.net

import android.util.Log

/**
 * A simple implementation of [DataUsageAlertListener] that just logs the alert as an error
 * when triggered.
 */
class LogDataUsageAlertListener : DataUsageAlertListener {

    private var tag: String? = null

    /**
     * Constructs a new listener that logs with the default tag "DataUsageWarning".
     */
    constructor() {
        this.tag = TAG
    }

    /**
     * Constructs a new listener that logs with the given tag.
     *
     * @param tag the tag to log at.
     */
    constructor(tag: String?) {
        if (tag == null) {
            throw IllegalArgumentException("tag cannot be null")
        }
        this.tag = tag
    }

    override fun alertThreshold(threshold: Threshold, dataUsage: DataUsage) {
        Log.e(tag, dataUsage.warningMessage)
    }

    companion object {
        private val TAG = "DataUsageWarning"
    }
}

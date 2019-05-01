package com.hex.evegate.net

/**
 * A listener that will be called when data usage reaches a threshold.
 */
interface DataUsageAlertListener {
    /**
     * Called when data usage reaches a threshold.
     *
     * @param threshold the threshold reached
     * @param dataUsage the data used
     */
    fun alertThreshold(threshold: Threshold, dataUsage: DataUsage)
}

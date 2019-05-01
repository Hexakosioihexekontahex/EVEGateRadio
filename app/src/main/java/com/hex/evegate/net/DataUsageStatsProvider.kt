package com.hex.evegate.net

/**
 * Created by evantatarka on 10/8/14.
 */
interface DataUsageStatsProvider {
    val nanoTime: Long
    val bytesTransmitted: Long
    val bytesReceived: Long
}

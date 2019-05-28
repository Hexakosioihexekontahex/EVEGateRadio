package com.hex.evegate.util

import com.hex.evegate.api.dto.NowPlaying

public fun calculateProgressPercent(startTime: Long, duration: Long) : Float {
    val now = System.currentTimeMillis() / 1000
    if (now <= startTime || duration == 0L) {
        return 0F
    }
    if (now >= startTime + duration) {
        return 100F
    }
    return (((now - startTime).toFloat() / duration) * 100)
}

public fun calculateProgressPercent(song: NowPlaying) : Float {
    return try {
        val startTime = song.played_at.toLong()
        val duration = song.duration.toLong()
        calculateProgressPercent(startTime, duration)
    } catch (e: NumberFormatException) {
        0F
    }
}
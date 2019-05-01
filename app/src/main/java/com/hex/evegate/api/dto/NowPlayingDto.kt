package com.hex.evegate.api.dto

import com.google.gson.annotations.SerializedName

data class NowPlayingDto (
    @SerializedName("cache") var cache: String,
    @SerializedName("listeners") var listeners: Listeners,
    @SerializedName("now_playing") var now_playing: NowPlaying,
    @SerializedName("song_history") var song_history: Array<SongHistory>,
    @SerializedName("station") var station: Station,
    @SerializedName("live") var live: Live,
    @SerializedName("playing_next") var playing_next: PlayingNext
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NowPlayingDto

        if (cache != other.cache) return false
        if (listeners != other.listeners) return false
        if (now_playing != other.now_playing) return false
        if (!song_history.contentEquals(other.song_history)) return false
        if (station != other.station) return false
        if (live != other.live) return false
        if (playing_next != other.playing_next) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cache.hashCode()
        result = 31 * result + listeners.hashCode()
        result = 31 * result + now_playing.hashCode()
        result = 31 * result + song_history.contentHashCode()
        result = 31 * result + station.hashCode()
        result = 31 * result + live.hashCode()
        result = 31 * result + playing_next.hashCode()
        return result
    }
}

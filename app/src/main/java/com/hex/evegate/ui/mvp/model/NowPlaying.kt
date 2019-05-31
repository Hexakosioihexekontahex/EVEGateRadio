package com.hex.evegate.ui.mvp.model

import com.google.gson.annotations.SerializedName

data class NowPlaying (
        @SerializedName("elapsed") var elapsed: String,
        @SerializedName("sh_id") var sh_id: String,
        @SerializedName("duration") var duration: String,
        @SerializedName("song") var song: Song,
        @SerializedName("is_request") var is_request: String,
        @SerializedName("playlist") var playlist: String,
        @SerializedName("played_at") var played_at: String,
        @SerializedName("remaining") var remaining: String
)

package com.hex.evegate.ui.mvp.model

import com.google.gson.annotations.SerializedName

data class Mounts (
    @SerializedName("name") var name: String,
    @SerializedName("format") var format: String,
    @SerializedName("bitrate") var bitrate: String,
    @SerializedName("is_default") var is_default: String,
    @SerializedName("url") var url: String
)

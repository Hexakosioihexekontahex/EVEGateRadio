package com.hex.evegate.ui.mvp.model

import com.google.gson.annotations.SerializedName

data class Live (
    @SerializedName("is_live") var is_live: String,
    @SerializedName("streamer_name") var streamer_name: String
)

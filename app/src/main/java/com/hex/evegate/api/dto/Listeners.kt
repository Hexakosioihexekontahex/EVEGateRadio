package com.hex.evegate.api.dto

import com.google.gson.annotations.SerializedName

data class Listeners (
    @SerializedName("current") var current: String,
    @SerializedName("total") var total: String,
    @SerializedName("unique") var unique: String
)

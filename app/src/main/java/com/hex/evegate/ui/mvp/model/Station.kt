package com.hex.evegate.ui.mvp.model

import com.google.gson.annotations.SerializedName

data class Station (
        @SerializedName("name") var name: String,
        @SerializedName("is_public") var is_public: String,
        @SerializedName("description") var description: String,
        @SerializedName("mounts") var mounts: Array<Mounts>,
        @SerializedName("remotes") var remotes: Array<String>,
        @SerializedName("backend") var backend: String,
        @SerializedName("id") var id: String,
        @SerializedName("shortcode") var shortcode: String,
        @SerializedName("listen_url") var listen_url: String,
        @SerializedName("frontend") var frontend: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Station

        if (name != other.name) return false
        if (is_public != other.is_public) return false
        if (description != other.description) return false
        if (!mounts.contentEquals(other.mounts)) return false
        if (!remotes.contentEquals(other.remotes)) return false
        if (backend != other.backend) return false
        if (id != other.id) return false
        if (shortcode != other.shortcode) return false
        if (listen_url != other.listen_url) return false
        if (frontend != other.frontend) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + is_public.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + mounts.contentHashCode()
        result = 31 * result + remotes.contentHashCode()
        result = 31 * result + backend.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + shortcode.hashCode()
        result = 31 * result + listen_url.hashCode()
        result = 31 * result + frontend.hashCode()
        return result
    }
}

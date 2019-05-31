package com.hex.evegate.ui.mvp.model

import com.google.gson.annotations.SerializedName

data class Song (
    @SerializedName("art") var art: String,
    @SerializedName("artist") var artist: String,
    @SerializedName("album") var album: String,
    @SerializedName("custom_fields") var custom_fields: Array<String>,
    @SerializedName("id") var id: String,
    @SerializedName("text") var text: String,
    @SerializedName("title") var title: String,
    @SerializedName("lyrics") var lyrics: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (art != other.art) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (!custom_fields.contentEquals(other.custom_fields)) return false
        if (id != other.id) return false
        if (text != other.text) return false
        if (title != other.title) return false
        if (lyrics != other.lyrics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = art.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + custom_fields.contentHashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + lyrics.hashCode()
        return result
    }
}

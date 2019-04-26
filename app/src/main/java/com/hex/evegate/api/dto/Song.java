package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("art") private String art;

    @SerializedName("artist") private String artist;

    @SerializedName("album") private String album;

    @SerializedName("custom_fields") private String[] custom_fields;

    @SerializedName("id") private String id;

    @SerializedName("text") private String text;

    @SerializedName("title") private String title;

    @SerializedName("lyrics") private String lyrics;

    public String getArt ()
    {
        return art;
    }

    public void setArt (String art)
    {
        this.art = art;
    }

    public String getArtist ()
    {
        return artist;
    }

    public void setArtist (String artist)
    {
        this.artist = artist;
    }

    public String getAlbum ()
    {
        return album;
    }

    public void setAlbum (String album)
    {
        this.album = album;
    }

    public String[] getCustom_fields ()
    {
        return custom_fields;
    }

    public void setCustom_fields (String[] custom_fields)
    {
        this.custom_fields = custom_fields;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getLyrics ()
    {
        return lyrics;
    }

    public void setLyrics (String lyrics)
    {
        this.lyrics = lyrics;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [art = "+art+", artist = "+artist+", album = "+album+", custom_fields = "+custom_fields+", id = "+id+", text = "+text+", title = "+title+", lyrics = "+lyrics+"]";
    }
}

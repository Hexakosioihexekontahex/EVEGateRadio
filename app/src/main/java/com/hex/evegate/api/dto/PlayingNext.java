package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class PlayingNext {
    @SerializedName("sh_id") private String sh_id;

    @SerializedName("duration") private String duration;

    @SerializedName("song") private Song song;

    @SerializedName("is_request") private String is_request;

    @SerializedName("playlist") private String playlist;

    @SerializedName("played_at") private String played_at;

    public String getSh_id ()
    {
        return sh_id;
    }

    public void setSh_id (String sh_id)
    {
        this.sh_id = sh_id;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public Song getSong ()
    {
        return song;
    }

    public void setSong (Song song)
    {
        this.song = song;
    }

    public String getIs_request ()
    {
        return is_request;
    }

    public void setIs_request (String is_request)
    {
        this.is_request = is_request;
    }

    public String getPlaylist ()
    {
        return playlist;
    }

    public void setPlaylist (String playlist)
    {
        this.playlist = playlist;
    }

    public String getPlayed_at ()
    {
        return played_at;
    }

    public void setPlayed_at (String played_at)
    {
        this.played_at = played_at;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sh_id = "+sh_id+", duration = "+duration+", song = "+song+", is_request = "+is_request+", playlist = "+playlist+", played_at = "+played_at+"]";
    }
}

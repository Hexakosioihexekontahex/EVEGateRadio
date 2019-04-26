package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class NowPlayingDto {

    @SerializedName("cache") private String cache;

    @SerializedName("listeners") private Listeners listeners;

    @SerializedName("now_playing") private NowPlaying now_playing;

    @SerializedName("song_history") private SongHistory[] song_history;

    @SerializedName("station") private Station station;

    @SerializedName("live") private Live live;

    @SerializedName("playing_next") private PlayingNext playing_next;

    public String getCache ()
    {
        return cache;
    }

    public void setCache (String cache)
    {
        this.cache = cache;
    }

    public Listeners getListeners ()
    {
        return listeners;
    }

    public void setListeners (Listeners listeners)
    {
        this.listeners = listeners;
    }

    public NowPlaying getNow_playing ()
    {
        return now_playing;
    }

    public void setNow_playing (NowPlaying now_playing)
    {
        this.now_playing = now_playing;
    }

    public SongHistory[] getSong_history ()
    {
        return song_history;
    }

    public void setSong_history (SongHistory[] song_history)
    {
        this.song_history = song_history;
    }

    public Station getStation ()
    {
        return station;
    }

    public void setStation (Station station)
    {
        this.station = station;
    }

    public Live getLive ()
    {
        return live;
    }

    public void setLive (Live live)
    {
        this.live = live;
    }

    public PlayingNext getPlaying_next ()
    {
        return playing_next;
    }

    public void setPlaying_next (PlayingNext playing_next)
    {
        this.playing_next = playing_next;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cache = "+cache+", listeners = "+listeners+", now_playing = "+now_playing+", song_history = "+song_history+", station = "+station+", live = "+live+", playing_next = "+playing_next+"]";
    }
}

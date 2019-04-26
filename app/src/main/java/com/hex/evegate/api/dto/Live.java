package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class Live {
    @SerializedName("is_live") private String is_live;

    @SerializedName("streamer_name") private String streamer_name;

    public String getIs_live ()
    {
        return is_live;
    }

    public void setIs_live (String is_live)
    {
        this.is_live = is_live;
    }

    public String getStreamer_name ()
    {
        return streamer_name;
    }

    public void setStreamer_name (String streamer_name)
    {
        this.streamer_name = streamer_name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [is_live = "+is_live+", streamer_name = "+streamer_name+"]";
    }
}

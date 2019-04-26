package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class Mounts {
    @SerializedName("name") private String name;

    @SerializedName("format") private String format;

    @SerializedName("bitrate") private String bitrate;

    @SerializedName("is_default") private String is_default;

    @SerializedName("url") private String url;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFormat ()
    {
        return format;
    }

    public void setFormat (String format)
    {
        this.format = format;
    }

    public String getBitrate ()
    {
        return bitrate;
    }

    public void setBitrate (String bitrate)
    {
        this.bitrate = bitrate;
    }

    public String getIs_default ()
    {
        return is_default;
    }

    public void setIs_default (String is_default)
    {
        this.is_default = is_default;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", format = "+format+", bitrate = "+bitrate+", is_default = "+is_default+", url = "+url+"]";
    }
}

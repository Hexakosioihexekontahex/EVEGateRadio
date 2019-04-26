package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class Station {
    @SerializedName("name") private String name;

    @SerializedName("is_public") private String is_public;

    @SerializedName("description") private String description;

    @SerializedName("mounts") private Mounts[] mounts;

    @SerializedName("remotes") private String[] remotes;

    @SerializedName("backend") private String backend;

    @SerializedName("id") private String id;

    @SerializedName("shortcode") private String shortcode;

    @SerializedName("listen_url") private String listen_url;

    @SerializedName("frontend") private String frontend;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getIs_public ()
    {
        return is_public;
    }

    public void setIs_public (String is_public)
    {
        this.is_public = is_public;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Mounts[] getMounts ()
    {
        return mounts;
    }

    public void setMounts (Mounts[] mounts)
    {
        this.mounts = mounts;
    }

    public String[] getRemotes ()
    {
        return remotes;
    }

    public void setRemotes (String[] remotes)
    {
        this.remotes = remotes;
    }

    public String getBackend ()
    {
        return backend;
    }

    public void setBackend (String backend)
    {
        this.backend = backend;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getShortcode ()
    {
        return shortcode;
    }

    public void setShortcode (String shortcode)
    {
        this.shortcode = shortcode;
    }

    public String getListen_url ()
    {
        return listen_url;
    }

    public void setListen_url (String listen_url)
    {
        this.listen_url = listen_url;
    }

    public String getFrontend ()
    {
        return frontend;
    }

    public void setFrontend (String frontend)
    {
        this.frontend = frontend;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", is_public = "+is_public+", description = "+description+", mounts = "+mounts+", remotes = "+remotes+", backend = "+backend+", id = "+id+", shortcode = "+shortcode+", listen_url = "+listen_url+", frontend = "+frontend+"]";
    }
}

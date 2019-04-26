package com.hex.evegate.api.dto;

import com.google.gson.annotations.SerializedName;

public class Listeners {
    @SerializedName("current") private String current;

    @SerializedName("total") private String total;

    @SerializedName("unique") private String unique;

    public String getCurrent ()
    {
        return current;
    }

    public void setCurrent (String current)
    {
        this.current = current;
    }

    public String getTotal ()
    {
        return total;
    }

    public void setTotal (String total)
    {
        this.total = total;
    }

    public String getUnique ()
    {
        return unique;
    }

    public void setUnique (String unique)
    {
        this.unique = unique;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [current = "+current+", total = "+total+", unique = "+unique+"]";
    }
}

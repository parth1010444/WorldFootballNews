package com.example.worldfootballnews;

import com.google.gson.annotations.SerializedName;

public class EspnLinks {
    @SerializedName("web")
    private EspnWebLink web;

    public EspnWebLink getWeb() {
        return web;
    }
}

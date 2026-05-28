package com.example.worldfootballnews;

import com.google.gson.annotations.SerializedName;

public class EspnImage {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}

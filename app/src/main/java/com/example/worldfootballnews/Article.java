package com.example.worldfootballnews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Article {
    @SerializedName("headline")
    private String headline;

    @SerializedName("description")
    private String description;

    @SerializedName("published")
    private String published;

    @SerializedName("images")
    private List<EspnImage> images;

    @SerializedName("links")
    private EspnLinks links;

    public String getTitle() {
        return headline;
    }

    public String getDescription() {
        return description;
    }

    public String getPublished() {
        return published;
    }

    public String getImageUrl() {
        if (images == null || images.isEmpty()) {
            return null;
        }

        return images.get(0).getUrl();
    }

    public String getUrl() {
        if (links == null || links.getWeb() == null) {
            return null;
        }

        return links.getWeb().getHref();
    }
}

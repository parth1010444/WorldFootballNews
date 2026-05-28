package com.example.worldfootballnews;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    @JsonProperty("headline")
    @JsonAlias({"title"})
    private String headline;

    @JsonProperty("description")
    private String description;

    @JsonProperty("published")
    private String published;

    @JsonProperty("images")
    private List<EspnImage> images;

    @JsonProperty("links")
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

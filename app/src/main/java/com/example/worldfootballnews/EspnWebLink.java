package com.example.worldfootballnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EspnWebLink {
    @JsonProperty("href")
    private String href;

    public String getHref() {
        return href;
    }
}

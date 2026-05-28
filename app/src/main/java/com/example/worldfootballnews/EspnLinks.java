package com.example.worldfootballnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EspnLinks {
    @JsonProperty("web")
    private EspnWebLink web;

    public EspnWebLink getWeb() {
        return web;
    }
}

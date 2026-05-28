package com.example.worldfootballnews;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsResponse {
    @JsonProperty("articles")
    @JsonAlias({"headlines"})
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
}

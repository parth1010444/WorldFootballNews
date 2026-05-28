package com.example.worldfootballnews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {
    @SerializedName(value = "articles", alternate = {"headlines"})
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
}

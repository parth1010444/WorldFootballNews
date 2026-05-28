package com.example.worldfootballnews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v1/sports/news")
    Call<NewsResponse> getSoccerNews(
            @Query("sport") String sport,
            @Query("limit") int limit
    );
}

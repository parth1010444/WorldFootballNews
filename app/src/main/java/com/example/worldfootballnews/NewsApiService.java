package com.example.worldfootballnews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("apis/site/v2/sports/soccer/{league}/news")
    Call<NewsResponse> getSoccerNews(
            @Path("league") String league,
            @Query("limit") int limit
    );
}

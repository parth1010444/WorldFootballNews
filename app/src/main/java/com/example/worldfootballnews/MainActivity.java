package com.example.worldfootballnews;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private NewsAdapter newsAdapter;
    private ProgressBar loadingIndicator;
    private TextView messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loadingIndicator);
        messageText = findViewById(R.id.messageText);

        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(new ArrayList<>());
        newsRecyclerView.setAdapter(newsAdapter);

        fetchFootballNews();
    }

    private void fetchFootballNews() {
        showLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://now.core.api.espn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);
        apiService.getSoccerNews("soccer", 50)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                        showLoading(false);

                        if (!response.isSuccessful() || response.body() == null) {
                            showMessage("Could not load football news. Please try again later.");
                            return;
                        }

                        List<Article> articles = response.body().getArticles();
                        if (articles == null || articles.isEmpty()) {
                            showMessage("No football news found right now.");
                            return;
                        }

                        messageText.setVisibility(View.GONE);
                        newsAdapter.updateArticles(articles);
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable throwable) {
                        showLoading(false);
                        showMessage("Network error. Check your internet connection and try again.");
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            messageText.setVisibility(View.GONE);
        }
    }

    private void showMessage(String message) {
        loadingIndicator.setVisibility(View.GONE);
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
    }
}

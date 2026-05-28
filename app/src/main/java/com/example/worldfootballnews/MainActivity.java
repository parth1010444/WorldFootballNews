package com.example.worldfootballnews;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private NewsAdapter newsAdapter;
    private ProgressBar pullToRefreshIndicator;
    private ProgressBar loadingIndicator;
    private TextView messageText;
    private Spinner leagueSpinner;
    private boolean isRefreshing = false;
    private float startY;

    private String selectedLeague = "eng.1";
    private final String[] leagueNames = {
            "FIFA World Cup", "UEFA Champions League", "English Premier League",
            "Spanish LALIGA", "German Bundesliga", "Italian Serie A",
            "French Ligue 1", "MLS", "Liga MX", "NWSL",
            "UEFA Europa League", "FIFA Women's World Cup"
    };
    private final String[] leagueIds = {
            "fifa.world", "uefa.champions", "eng.1",
            "esp.1", "ger.1", "ita.1",
            "fra.1", "usa.1", "mex.1", "usa.nwsl",
            "uefa.europa", "fifa.wwc"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToRefreshIndicator = findViewById(R.id.pullToRefreshIndicator);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        messageText = findViewById(R.id.messageText);
        leagueSpinner = findViewById(R.id.leagueSpinner);

        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(new ArrayList<>());
        newsRecyclerView.setAdapter(newsAdapter);

        setupLeagueSpinner();

        setupPullToRefresh(newsRecyclerView);
    }

    private void setupLeagueSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_selected, leagueNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView != null) {
                    return convertView;
                }
                return getLayoutInflater().inflate(R.layout.spinner_item_selected, parent, false);
            }

            @NonNull
            @Override
            public View getDropDownView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof CheckedTextView) {
                    ((CheckedTextView) view).setChecked(position == getSelectedItemPosition());
                }

                return view;
            }

            private int getSelectedItemPosition() {
                for (int i = 0; i < leagueIds.length; i++) {
                    if (leagueIds[i].equals(selectedLeague)) {
                        return i;
                    }
                }
                return 0;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        leagueSpinner.setAdapter(adapter);

        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newLeague = leagueIds[position];
                if (!newLeague.equals(selectedLeague) || newsAdapter.getItemCount() == 0) {
                    selectedLeague = newLeague;
                    fetchFootballNews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPullToRefresh(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                if (isRefreshing) return false;

                switch (e.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        startY = e.getY();
                        break;
                    case android.view.MotionEvent.ACTION_MOVE:
                        float currentY = e.getY();
                        if (!rv.canScrollVertically(-1) && currentY > startY + 300) {
                            // User pulled down significantly at the top
                            fetchFootballNews();
                            return true; // Intercept and consume this touch
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void fetchFootballNews() {
        showLoading(true);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "WorldFootballNews/1.0")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://site.api.espn.com/")
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);
        apiService.getSoccerNews(selectedLeague, 50)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                        Log.d("MainActivity", "onResponse: " + response.code());
                        showLoading(false);

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("MainActivity", "Response unsuccessful: " + response.code());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e("MainActivity", "Error body: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                Log.e("MainActivity", "Error reading error body", e);
                            }
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
                        Log.e("MainActivity", "Error fetching news", throwable);
                        showMessage("Network error. Check your internet connection and try again.");
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        isRefreshing = isLoading;
        if (isLoading) {
            if (newsAdapter.getItemCount() == 0) {
                loadingIndicator.setVisibility(View.VISIBLE);
            } else {
                pullToRefreshIndicator.setVisibility(View.VISIBLE);
            }
            messageText.setVisibility(View.GONE);
        } else {
            pullToRefreshIndicator.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.GONE);
            // Removed: appBarLayout.setExpanded(true, true);
        }
    }

    private void showMessage(String message) {
        loadingIndicator.setVisibility(View.GONE);
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
    }
}

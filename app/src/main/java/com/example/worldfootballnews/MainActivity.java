package com.example.worldfootballnews;

import android.os.Bundle;
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
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private NewsAdapter newsAdapter;
    private AppBarLayout appBarLayout;
    private ProgressBar pullToRefreshIndicator;
    private ProgressBar loadingIndicator;
    private TextView messageText;
    private Spinner leagueSpinner;
    private boolean isRefreshing = false;
    private float startY;

    private String selectedLeague = "fifa.world";
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

        appBarLayout = findViewById(R.id.appBarLayout);
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

                // Handle selection via touch event
                view.setOnTouchListener((v, event) -> {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        v.performClick();
                        leagueSpinner.setSelection(position);
                    }
                    return false; // Return false to allow the Spinner to also handle the click/dismiss
                });

                return view;
            }

            private int getSelectedItemPosition() {
                for (int j = 0; j < leagueIds.length; j++) {
                    if (leagueIds[j].equals(selectedLeague)) {
                        return j;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://site.api.espn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);
        apiService.getSoccerNews(selectedLeague, 50)
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
            appBarLayout.setExpanded(true, true);
        }
    }

    private void showMessage(String message) {
        loadingIndicator.setVisibility(View.GONE);
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
    }
}

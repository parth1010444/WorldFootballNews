package com.example.worldfootballnews;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<Article> articles;

    public NewsAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void updateArticles(List<Article> nextArticles) {
        articles.clear();
        articles.addAll(nextArticles);
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView articleImage;
        private final TextView articleTitle;
        private final TextView articleSource;
        private final TextView articleDescription;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.articleImage);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSource = itemView.findViewById(R.id.articleSource);
            articleDescription = itemView.findViewById(R.id.articleDescription);
        }

        void bind(Article article) {
            articleTitle.setText(textOrFallback(article.getTitle(), "Untitled story"));
            articleDescription.setText(textOrFallback(article.getDescription(), "Open this story to read more."));
            articleSource.setText(formatSource(article.getPublished()));

            Glide.with(articleImage)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(articleImage);

            itemView.setOnClickListener(view -> {
                if (TextUtils.isEmpty(article.getUrl())) {
                    return;
                }

                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, article.getUrl());
                view.getContext().startActivity(intent);
            });
        }

        private String textOrFallback(String value, String fallback) {
            if (value == null || value.trim().isEmpty()) {
                return fallback;
            }
            return value;
        }

        private String formatSource(String published) {
            if (published == null || published.trim().isEmpty()) {
                return "ESPN Soccer";
            }

            return "ESPN Soccer - " + published.substring(0, Math.min(10, published.length()));
        }
    }
}

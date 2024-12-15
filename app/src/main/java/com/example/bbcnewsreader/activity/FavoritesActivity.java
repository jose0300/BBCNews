package com.example.bbcnewsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.adapters.NewsAdapter;
import com.example.bbcnewsreader.data.DatabaseHelper;
import com.example.bbcnewsreader.data.NewsItem;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;
    private DatabaseHelper databaseHelper;
    private List<NewsItem> favoriteArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        // Load favorites initially
        loadFavoriteArticles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when coming back to this activity
        loadFavoriteArticles();
    }

    private void loadFavoriteArticles() {
        progressBar.setVisibility(View.VISIBLE);
        favoriteArticles = databaseHelper.getAllFavorites();

        if (favoriteArticles.isEmpty()) {
            Toast.makeText(this, R.string.no_favorites, Toast.LENGTH_SHORT).show();
        }

        newsAdapter = new NewsAdapter(this, favoriteArticles, newsItem -> {
            // Open ArticleDetailActivity with the selected article
            Intent intent = new Intent(FavoritesActivity.this, ArticleDetailActivity.class);
            intent.putExtra("TITLE", newsItem.getTitle());
            intent.putExtra("DESCRIPTION", newsItem.getDescription());
            intent.putExtra("DATE", newsItem.getDate());
            intent.putExtra("URL", newsItem.getUrl());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


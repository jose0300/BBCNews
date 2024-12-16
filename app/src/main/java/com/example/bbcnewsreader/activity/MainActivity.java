package com.example.bbcnewsreader.activity;

import com.example.bbcnewsreader.adapters.NewsAdapter;
import com.example.bbcnewsreader.network.NewsFetcher;

import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbcnewsreader.R;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private boolean isFavoriteView = false;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved language preference
        loadLanguage();

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>(), newsItem -> {
            Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
            intent.putExtra("TITLE", newsItem.getTitle());
            intent.putExtra("DESCRIPTION", newsItem.getDescription());
            intent.putExtra("DATE", newsItem.getDate());
            intent.putExtra("URL", newsItem.getUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(newsAdapter);

        // Fetch news using NewsFetcher
        fetchNews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            if (isFavoriteView) {
                isFavoriteView = false;
                fetchNews();
            } else {
                isFavoriteView = true;
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_about) {
            // Launch AboutActivity
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_language) {
            // Toggle language between English and French
            String currentLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
            if (currentLanguage.equals("en")) {
                setLocale("fr");
            } else {
                setLocale("en");
            }
            return true;
        } else if (id == R.id.action_theme) {
            // Toggle theme
            boolean isDarkMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
            toggleTheme(!isDarkMode);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        config.setLocale(locale);
        resources.updateConfiguration(config, dm);

        // Save language preference
        getSharedPreferences("AppSettings", MODE_PRIVATE)
                .edit()
                .putString("Language", langCode)
                .apply();

        // Display a toast message when switching languages
        if (langCode.equals("en")) {
            Toast.makeText(this, "Language switched to English", Toast.LENGTH_SHORT).show();
        } else if (langCode.equals("fr")) {
            Toast.makeText(this, "Langue changée en Français", Toast.LENGTH_SHORT).show();
        }

        // Reload activity
        recreate();
    }

    private void loadLanguage() {
        String langCode = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("Language", "en");
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
    }

    private void fetchNews() {
        new NewsFetcher(progressBar, newsAdapter, new NewsFetcher.Callback() {
            @Override
            public void onFetchCompleted() {
                // Optional: Add extra actions if needed
            }

            @Override
            public void onFetchFailed() {
                Toast.makeText(MainActivity.this, getString(R.string.failed_to_load_news), Toast.LENGTH_SHORT).show();
            }
        }).execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
    }

    private void toggleTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate(); // Recreate the activity to apply the theme
    }
}








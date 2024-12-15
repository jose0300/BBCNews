package com.example.bbcnewsreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.data.DatabaseHelper;
import com.example.bbcnewsreader.data.NewsItem;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private Button saveButton;
    private Button goToArticleButton;

    private boolean isSaved = false;
    private String articleUrl;
    private NewsItem currentNewsItem;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        saveButton = findViewById(R.id.saveButton);
        goToArticleButton = findViewById(R.id.goToArticleButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set up the toolbar with the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        // Get article data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("TITLE");
            String description = intent.getStringExtra("DESCRIPTION");
            String date = intent.getStringExtra("DATE");
            articleUrl = intent.getStringExtra("URL");

            currentNewsItem = new NewsItem(title, description, date, articleUrl);

            // Set the views with data
            titleTextView.setText(title);
            descriptionTextView.setText(description);
            dateTextView.setText(date);

            // Check if the article is saved
            isSaved = databaseHelper.isArticleSaved(title);
            updateSaveButton();
        }

        // Save/Unsave button logic
        saveButton.setOnClickListener(v -> {
            if (isSaved) {
                databaseHelper.removeFavorite(currentNewsItem.getTitle());
                isSaved = false;
                Toast.makeText(this, R.string.article_unsaved, Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.addFavorite(currentNewsItem);
                isSaved = true;
                Toast.makeText(this, R.string.article_saved, Toast.LENGTH_SHORT).show();
            }
            updateSaveButton();
        });

        // Go to Article button logic
        goToArticleButton.setOnClickListener(v -> {
            if (articleUrl != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSaveButton() {
        if (isSaved) {
            saveButton.setText(R.string.article_unsave);
        } else {
            saveButton.setText(R.string.article_save);
        }
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


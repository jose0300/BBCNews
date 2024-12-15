package com.example.bbcnewsreader.network;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bbcnewsreader.adapters.NewsAdapter;
import com.example.bbcnewsreader.data.NewsItem;
import com.example.bbcnewsreader.data.NewsParser;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NewsFetcher extends AsyncTask<String, Void, List<NewsItem>> {

    private final ProgressBar progressBar;
    private final NewsAdapter newsAdapter;
    private final Callback callback;

    public interface Callback {
        void onFetchCompleted();
        void onFetchFailed();
    }

    public NewsFetcher(ProgressBar progressBar, NewsAdapter newsAdapter, Callback callback) {
        this.progressBar = progressBar;
        this.newsAdapter = newsAdapter;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected List<NewsItem> doInBackground(String... urls) {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            NewsParser newsParser = new NewsParser();

            saxParser.parse(new InputSource(inputStream), newsParser);
            newsItems = newsParser.getNewsItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsItems;
    }

    @Override
    protected void onPostExecute(List<NewsItem> result) {
        super.onPostExecute(result);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (result != null && !result.isEmpty()) {
            if (newsAdapter != null) {
                newsAdapter.updateNewsItems(result);
            }
            if (callback != null) {
                callback.onFetchCompleted();
            }
        } else {
            if (callback != null) {
                callback.onFetchFailed();
            }
        }
    }
}








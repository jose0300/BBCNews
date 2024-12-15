package com.example.bbcnewsreader.network;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchFeedTask extends AsyncTask<String, Void, List<String[]>> {

    private final Listener listener;

    public interface Listener {
        void onFetchSuccess(List<String[]> rssItems);
        void onFetchError();
    }

    public FetchFeedTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected List<String[]> doInBackground(String... strings) {
        List<String[]> rssItems = new ArrayList<>();
        String rssUrl = strings[0];

        try {
            // Open connection to the RSS URL
            URL url = new URL(rssUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            // Parse XML response
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            String title = null, description = null, link = null, pubDate = null;
            boolean insideItem = false;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (eventType == XmlPullParser.START_TAG) {
                    if ("item".equals(tagName)) {
                        insideItem = true;
                    } else if ("title".equals(tagName) && insideItem) {
                        title = parser.nextText();
                    } else if ("description".equals(tagName) && insideItem) {
                        description = parser.nextText();
                    } else if ("link".equals(tagName) && insideItem) {
                        link = parser.nextText();
                    } else if ("pubDate".equals(tagName) && insideItem) {
                        pubDate = parser.nextText();
                    }
                } else if (eventType == XmlPullParser.END_TAG && "item".equals(tagName)) {
                    rssItems.add(new String[]{title, description, link, pubDate});
                    insideItem = false;
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rssItems;
    }

    @Override
    protected void onPostExecute(List<String[]> rssItems) {
        if (rssItems.isEmpty()) {
            listener.onFetchError();
        } else {
            listener.onFetchSuccess(rssItems);
        }
    }
}

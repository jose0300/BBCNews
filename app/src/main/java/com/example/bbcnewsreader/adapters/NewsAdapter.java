package com.example.bbcnewsreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.data.NewsItem;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsItem> newsItems;
    private OnNewsItemClickListener listener;

    public NewsAdapter(Context context, List<NewsItem> newsItems, OnNewsItemClickListener listener) {
        this.context = context;
        this.newsItems = newsItems;
        this.listener = listener;
    }

    public List<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void updateNewsItems(List<NewsItem> newItems) {
        this.newsItems.clear();
        this.newsItems.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItems.get(position);
        holder.title.setText(newsItem.getTitle());
        holder.date.setText(newsItem.getDate());
        holder.itemView.setOnClickListener(v -> listener.onNewsItemClick(newsItem));
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            date = itemView.findViewById(R.id.newsDate);
        }
    }
}


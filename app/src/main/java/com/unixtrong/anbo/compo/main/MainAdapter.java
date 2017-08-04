package com.unixtrong.anbo.compo.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.entity.Feed;

import java.util.List;

/**
 * Created by danyun on 2017/8/5
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.FeedHolder> {
    private Context mContext;
    private List<Feed> mFeedList;
    private LayoutInflater mInflater;

    public MainAdapter(Context context, List<Feed> feedList) {
        this.mContext = context;
        this.mFeedList = feedList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedHolder(mInflater.inflate(R.layout.adapter_main_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        Feed feed = mFeedList.get(position);
        holder.nameTextView.setText(feed.getUser().getName());
        holder.dateTextView.setText(feed.getTime());
        holder.contentTextView.setText(feed.getContent());
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    static class FeedHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView contentTextView;

        FeedHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_main_name);
            dateTextView = (TextView) itemView.findViewById(R.id.tv_main_date);
            contentTextView = (TextView) itemView.findViewById(R.id.tv_main_content);
        }
    }
}

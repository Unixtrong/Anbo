package com.unixtrong.anbo.compo.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.entity.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by danyun on 2017/8/5
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.FeedHolder> {
    /**
     * 格式化时间文本工具
     * 用于较早发布的微博
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
    /**
     * 格式化时间文本工具
     * 用于最近发布的微博
     */
    private DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private Context mContext;
    private List<Feed> mFeedList;
    private LayoutInflater mInflater;

    private Date mRequestDate;

    MainAdapter(Context context, List<Feed> feedList) {
        this.mContext = context;
        this.mFeedList = feedList;
        this.mInflater = LayoutInflater.from(context);
        mRequestDate = new Date();
    }

    /**
     * 更新页面的请求时间，用于每条微博的时间展示（如「5 秒前」「20 分钟前」）
     *
     * @param date 最近一次请求的时间
     */
    void updateLastRequestTime(Date date) {
        mRequestDate = date;
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedHolder(mInflater.inflate(R.layout.adapter_main_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        Feed feed = mFeedList.get(position);
        String userName = getSafeUserName(feed.getUser());
        holder.mNameTextView.setVisibility(userName != null ? View.VISIBLE : View.GONE);
        holder.mNameTextView.setText(userName);
        holder.mContentTextView.setText(feed.getContent());
        holder.mDateTextView.setText(getDisplayTime(feed));

        if (feed.getRetweet() != null) {
            holder.mRetweetLayout.setVisibility(View.VISIBLE);
            String retweetUserName = getSafeUserName(feed.getRetweet().getUser());
            holder.mRetweetNameTextView.setVisibility(retweetUserName != null ? View.VISIBLE : View.GONE);
            holder.mRetweetNameTextView.setText(mContext.getString(R.string.main_adapter_retweet_user, retweetUserName));
            holder.mRetweetContentTextView.setText(feed.getRetweet().getContent());
        } else {
            holder.mRetweetLayout.setVisibility(View.GONE);
            holder.mRetweetNameTextView.setText("");
            holder.mRetweetContentTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    private String getSafeUserName(User user) {
        if (user != null) {
            return user.getName();
        } else {
            return null;
        }
    }

    /**
     * 根据 Feed 信息获取需要展示的时间文本
     */
    private String getDisplayTime(Feed feed) {
        // 微博发布时间的 long 型时间戳
        long feedTime = feed.getDate().getTime();
        // 用户最近一次请求时间 和 微博发布时间 的差值
        long timeDiff = mRequestDate.getTime() - feedTime;
        // 用户最近一次请求时间 所对应的天数
        long requestDay = (mRequestDate.getTime() + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);
        // 微博发布时间 所对应的天数
        long feedDay = (feedTime + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);

        String displayTime;
        if (requestDay == feedDay) {
            if (timeDiff < TimeUnit.MINUTES.toMillis(1)) {
                displayTime = TimeUnit.MILLISECONDS.toSeconds(timeDiff) + " 秒前";
            } else if (timeDiff < TimeUnit.HOURS.toMillis(1)) {
                displayTime = TimeUnit.MILLISECONDS.toMinutes(timeDiff) + " 分钟前";
            } else {
                displayTime = TIME_FORMAT.format(feed.getDate());
            }
        } else if (requestDay - feedDay == 1) {
            displayTime = "昨天 " + TIME_FORMAT.format(feed.getDate());
        } else if (requestDay - feedDay == 2) {
            displayTime = "前天" + TIME_FORMAT.format(feed.getDate());
        } else {
            displayTime = DATE_FORMAT.format(feed.getDate());
        }

        return displayTime;
    }

    static class FeedHolder extends RecyclerView.ViewHolder {
        TextView mNameTextView;
        TextView mDateTextView;
        TextView mContentTextView;
        ViewGroup mRetweetLayout;
        TextView mRetweetNameTextView;
        TextView mRetweetContentTextView;

        FeedHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_main_name);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_main_date);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_main_content);
            mRetweetLayout = (ViewGroup) itemView.findViewById(R.id.rl_main_retweet);
            mRetweetNameTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet_name);
            mRetweetContentTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet);
        }
    }
}

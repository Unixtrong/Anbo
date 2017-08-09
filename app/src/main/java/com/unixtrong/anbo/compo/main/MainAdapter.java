package com.unixtrong.anbo.compo.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.entity.User;
import com.unixtrong.anbo.handler.PicLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    /**
     * 头像大小正则模板
     */
    private static final Pattern PATTERN = Pattern.compile("crop\\.(\\d+\\.){4}(\\d+)");

    private static Date sRequestDate;
    private Context mContext;
    private List<Feed> mFeedList;
    private LayoutInflater mInflater;

    MainAdapter(Context context, List<Feed> feedList) {
        this.mContext = context;
        this.mFeedList = feedList;
        this.mInflater = LayoutInflater.from(context);
        sRequestDate = new Date();
    }

    private static String userName(User user) {
        if (user != null) {
            return user.getName();
        } else {
            return null;
        }
    }

    private static String avatarThumbUrl(User user) {
        if (user != null) {
            String avatar = user.getAvatar();
            Matcher matcher = PATTERN.matcher(avatar);
            if (matcher.find()) {
                String cropStr = matcher.group();
                String newCrop = cropStr.substring(0, cropStr.lastIndexOf(".")) + ".120";
                avatar = avatar.replaceAll(cropStr, newCrop);
            }
            return avatar;
        } else {
            return null;
        }
    }

    /**
     * 根据 Feed 信息获取需要展示的时间文本
     */
    private static String getDisplayTime(Feed feed) {
        // 微博发布时间的 long 型时间戳，即该时间距离 1970 年 1 月 1 日上午 8 点所产生的毫秒数
        long feedTime = feed.getDate().getTime();
        // 【用户最近一次请求时间】和【微博发布时间】的差值
        long timeDiff = sRequestDate.getTime() - feedTime;
        // 用户最近一次请求时间所对应的天数 =（请求时间毫秒数 + 八小时毫秒数）/ 一天毫秒数
        long requestDay = (sRequestDate.getTime() + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);
        // 微博发布时间所对应的天数 =（微博发布时间毫秒数 + 八小时毫秒数）/ 一天毫秒数
        long feedDay = (feedTime + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);

        String displayTime;
        if (requestDay == feedDay) {
            // 当微博时间和请求时间是同一天时
            if (timeDiff < TimeUnit.MINUTES.toMillis(1)) {
                // 当微博时间和请求时间相差 1 分钟以内时，展示示例：「23 秒前」
                displayTime = TimeUnit.MILLISECONDS.toSeconds(timeDiff) + " 秒前";
            } else if (timeDiff < TimeUnit.HOURS.toMillis(1)) {
                // 当微博时间和请求时间相差 1 小时以内时，展示示例：「4 分钟前」
                displayTime = TimeUnit.MILLISECONDS.toMinutes(timeDiff) + " 分钟前";
            } else {
                // 当微博时间和请求时间相差 2 小时及以上时，展示示例：「11:09」
                displayTime = TIME_FORMAT.format(feed.getDate());
            }
        } else if (requestDay - feedDay == 1) {
            // 当微博时间比请求时间早 1 天时，展示示例：「昨天 05:31」
            displayTime = "昨天 " + TIME_FORMAT.format(feed.getDate());
        } else if (requestDay - feedDay == 2) {
            // 当微博时间比请求时间早 2 天时，展示示例：「前天 14:23」
            displayTime = "前天" + TIME_FORMAT.format(feed.getDate());
        } else {
            // 当微博时间比请求时间早 3 天及以上时，展示示例：「17-07-23 11:09」
            displayTime = DATE_FORMAT.format(feed.getDate());
        }

        return displayTime;
    }

    /**
     * 更新页面的请求时间，用于每条微博的时间展示（如「5 秒前」「20 分钟前」）
     *
     * @param date 最近一次请求的时间
     */
    void updateLastRequestTime(Date date) {
        sRequestDate = date;
    }

    @Override
    public int getItemViewType(int position) {
        return mFeedList.get(position).getType();
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Feed.TYPE_PICTURE:
                return new PicHolder(mInflater.inflate(R.layout.adapter_main_item_pic, parent, false));
            case Feed.TYPE_RETWEET:
            default:
                return new TextHolder(mInflater.inflate(R.layout.adapter_main_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        holder.bind(mFeedList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    static class FeedHolder extends RecyclerView.ViewHolder {
        Context mContext;
        ImageView mAvatarImageView;
        TextView mNameTextView;
        TextView mDateTextView;
        TextView mContentTextView;

        FeedHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mAvatarImageView = (ImageView) itemView.findViewById(R.id.iv_main_avatar);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_main_name);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_main_date);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_main_content);
        }

        void bind(Feed feed) {
            String userName = userName(feed.getUser());
            mNameTextView.setVisibility(userName != null ? View.VISIBLE : View.GONE);
            mNameTextView.setText(userName);
            mContentTextView.setText(feed.getContent());
            mDateTextView.setText(getDisplayTime(feed));
            String avatarUrl = avatarThumbUrl(feed.getUser());
            PicLoader.with(mContext).bind(avatarUrl, mAvatarImageView, R.mipmap.ic_launcher_round);
        }
    }

    private class TextHolder extends FeedHolder {
        ViewGroup mRetweetLayout;
        TextView mRetweetNameTextView;
        TextView mRetweetContentTextView;

        TextHolder(View itemView) {
            super(itemView);
            mRetweetLayout = (ViewGroup) itemView.findViewById(R.id.rl_main_retweet);
            mRetweetNameTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet_name);
            mRetweetContentTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet);
        }

        void bind(Feed feed) {
            super.bind(feed);
            if (feed.getRetweet() != null) {
                mRetweetLayout.setVisibility(View.VISIBLE);
                String retweetUserName = userName(feed.getRetweet().getUser());
                mRetweetNameTextView.setVisibility(retweetUserName != null ? View.VISIBLE : View.GONE);
                mRetweetNameTextView.setText(mContext.getString(R.string.main_adapter_retweet_user, retweetUserName));
                mRetweetContentTextView.setText(feed.getRetweet().getContent());
            } else {
                mRetweetLayout.setVisibility(View.GONE);
                mRetweetNameTextView.setText("");
                mRetweetContentTextView.setText("");
            }
        }
    }

    private class PicHolder extends FeedHolder {

        ImageView mPicImageView;

        PicHolder(View itemView) {
            super(itemView);
            mPicImageView = (ImageView) itemView.findViewById(R.id.iv_main_pic);
        }

        void bind(Feed feed) {
            super.bind(feed);
            String[] pics = feed.getPics();
            String pic = pics.length != 0 ? pics[0] : "";
            pic = pic.replaceFirst("thumbnail", "wap720");
            PicLoader.with(mContext).bind(pic, mPicImageView, R.mipmap.ic_launcher);
        }
    }
}

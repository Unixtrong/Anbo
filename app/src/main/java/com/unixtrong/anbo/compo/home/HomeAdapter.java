package com.unixtrong.anbo.compo.home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unixtrong.anbo.R;
import com.unixtrong.anbo.compo.picture.LargePicActivity;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.entity.User;
import com.unixtrong.anbo.handler.PicLoader;
import com.unixtrong.anbo.tools.BaseRecyclerAdapter;
import com.unixtrong.anbo.tools.Lg;
import com.unixtrong.anbo.view.MultiPicLayout;

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

class HomeAdapter extends BaseRecyclerAdapter<Feed> {
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
    private static final Pattern AVATAR_PATTERN = Pattern.compile("crop\\.(\\d+\\.){4}(\\d+)");
    private static final String REGEX_USER = "@\\w+";
    private static final String REGEX_TAG = "#[^#]+#";
    private static final String REGEX_URL = "https?://[0-9a-zA-Z/.]+";

    private static Date sRequestDate;
    private static int sLinkColor;
    private LayoutInflater mInflater;

    HomeAdapter(Context context, List<Feed> feedList) {
        super(feedList);
        this.mInflater = LayoutInflater.from(context);
        sRequestDate = new Date();
        sLinkColor = ContextCompat.getColor(context, R.color.linkText);
    }

    @Override
    protected FeedHolder onItemCreate(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Feed.TYPE_PICTURE:
                return new PicHolder(mInflater.inflate(R.layout.adapter_main_item_pic, parent, false));
            case Feed.TYPE_MULTI_PICTURE:
                return new MultiPicHolder(mInflater.inflate(R.layout.adapter_main_item_multi_pics, parent, false));
            case Feed.TYPE_RETWEET_PICTURE:
                return new RetweetPicHolder(mInflater.inflate(R.layout.adapter_main_item_retweet_pic, parent, false));
            case Feed.TYPE_RETWEET_MULTI_PICTURE:
                return new RetweetMultiPicHolder(mInflater.inflate(R.layout.adapter_main_item_retweet_multi_pics, parent, false));
            case Feed.TYPE_RETWEET:
            default:
                return new TextHolder(mInflater.inflate(R.layout.adapter_main_item, parent, false));
        }
    }

    @Override
    protected void onItemBind(Holder holder, int position, Feed data) {
        if (holder instanceof FeedHolder) {
            ((FeedHolder) holder).bind(getData().get(position));
        }
    }

    @Override
    protected int getDataViewType(int position) {
        return getData().get(position).getType();
    }

    /**
     * 更新页面的请求时间，用于每条微博的时间展示（如「5 秒前」「20 分钟前」）
     *
     * @param date 最近一次请求的时间
     */
    void updateLastRequestTime(Date date) {
        sRequestDate = date;
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
            Matcher matcher = AVATAR_PATTERN.matcher(avatar);
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
     * 为内容中等用户名和链接添加颜色和点击时间
     *
     * @param text 原文本内容
     */
    private static CharSequence feedContent(String text) {
        Pattern pattern = Pattern.compile("(" + REGEX_USER + ")|(" + REGEX_TAG + ")|(" + REGEX_URL + ")");
        Matcher matcher = pattern.matcher(text);
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        while (matcher.find()) {
            String user = matcher.group(1);
            String tag = matcher.group(2);
            String url = matcher.group(3);
            if (user != null) {
                ForegroundColorSpan span = new ForegroundColorSpan(sLinkColor);
                int start = matcher.start(1);
                int end = matcher.end(1);
                Lg.debug("text: " + text + ", s: " + start + ", e: " + end);
                builder.setSpan(span, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (tag != null) {
                ForegroundColorSpan span = new ForegroundColorSpan(sLinkColor);
                int start = matcher.start(2);
                int end = matcher.end(2);
                Lg.debug("text: " + text + ", s: " + start + ", e: " + end);
                builder.setSpan(span, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (url != null) {
                ForegroundColorSpan span = new ForegroundColorSpan(sLinkColor);
                int start = matcher.start(3);
                int end = matcher.end(3);
                Lg.debug("text: " + text + ", s: " + start + ", e: " + end);
                builder.setSpan(span, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
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

    static class FeedHolder extends BaseRecyclerAdapter.Holder {
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
            mContentTextView.setText(feedContent(feed.getContent()));
            mDateTextView.setText(getDisplayTime(feed));
            String avatarUrl = avatarThumbUrl(feed.getUser());
            PicLoader.with(mContext).bind(avatarUrl, mAvatarImageView, R.mipmap.ic_launcher_round, 120, 120);
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
                mRetweetContentTextView.setText(feedContent(feed.getRetweet().getContent()));
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
            PicLoader.with(mContext).bind(pic, mPicImageView, R.mipmap.ic_launcher, 800, 800);
            mPicImageView.setOnClickListener(new OnPicClickListener(0, new String[]{pic}));
        }
    }

    private class MultiPicHolder extends FeedHolder {

        MultiPicLayout mPicsLayout;
        String[] mMiddlePics;

        MultiPicHolder(View itemView) {
            super(itemView);
            mPicsLayout = (MultiPicLayout) itemView.findViewById(R.id.mpl_main_pics);
        }

        void bind(Feed feed) {
            super.bind(feed);
            String[] pics = feed.getPics();
            mMiddlePics = new String[pics.length];
            ImageView[] imageViews = mPicsLayout.getImageViews();
            for (int i = 0; i < pics.length; i++) {
                String pic = pics[i].replaceFirst("thumbnail", "bmiddle");
                mMiddlePics[i] = pic;
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                PicLoader.with(mContext).bind(pic, imageView, R.mipmap.ic_launcher, 300, 300);
                imageView.setOnClickListener(new OnPicClickListener(i, mMiddlePics));
            }
            for (int i = pics.length; i < imageViews.length; i++) {
                imageViews[i].setVisibility(View.GONE);
            }
        }

    }

    private class RetweetPicHolder extends FeedHolder {

        TextView mRetweetNameTextView;
        TextView mRetweetContentTextView;
        ImageView mRetweetPicImageView;

        RetweetPicHolder(View itemView) {
            super(itemView);
            mRetweetNameTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet_name);
            mRetweetContentTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet);
            mRetweetPicImageView = (ImageView) itemView.findViewById(R.id.iv_main_pic);
        }

        void bind(Feed feed) {
            super.bind(feed);
            Feed retweet = feed.getRetweet();
            String retweetUserName = userName(retweet.getUser());
            mRetweetNameTextView.setText(mContext.getString(R.string.main_adapter_retweet_user, retweetUserName));
            mRetweetContentTextView.setText(feedContent(feed.getRetweet().getContent()));

            String[] pics = retweet.getPics();
            String pic = pics.length != 0 ? pics[0] : "";
            pic = pic.replaceFirst("thumbnail", "wap720");
            PicLoader.with(mContext).bind(pic, mRetweetPicImageView, R.mipmap.ic_launcher, 800, 800);
            mRetweetPicImageView.setOnClickListener(new OnPicClickListener(0, new String[]{pic}));
        }
    }

    private class RetweetMultiPicHolder extends FeedHolder {

        TextView mRetweetNameTextView;
        TextView mRetweetContentTextView;
        MultiPicLayout mRetweetPicsLayout;
        String[] mMiddlePics;

        RetweetMultiPicHolder(View itemView) {
            super(itemView);
            mRetweetNameTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet_name);
            mRetweetContentTextView = (TextView) itemView.findViewById(R.id.tv_main_retweet);
            mRetweetPicsLayout = (MultiPicLayout) itemView.findViewById(R.id.mpl_main_pics);
        }

        void bind(Feed feed) {
            super.bind(feed);
            Feed retweet = feed.getRetweet();
            String retweetUserName = userName(retweet.getUser());
            mRetweetNameTextView.setText(mContext.getString(R.string.main_adapter_retweet_user, retweetUserName));
            mRetweetContentTextView.setText(feedContent(feed.getRetweet().getContent()));

            String[] pics = retweet.getPics();
            mMiddlePics = new String[pics.length];
            ImageView[] imageViews = mRetweetPicsLayout.getImageViews();
            for (int i = 0; i < pics.length; i++) {
                String pic = pics[i].replaceFirst("thumbnail", "bmiddle");
                mMiddlePics[i] = pic;
                ImageView imageView = imageViews[i];
                imageView.setVisibility(View.VISIBLE);
                PicLoader.with(mContext).bind(pic, imageView, R.mipmap.ic_launcher, 300, 300);
                imageView.setOnClickListener(new OnPicClickListener(i, mMiddlePics));
            }
            for (int i = pics.length; i < imageViews.length; i++) {
                imageViews[i].setVisibility(View.GONE);
            }
        }
    }

    private class OnPicClickListener implements View.OnClickListener {

        private int mCurrentIndex;
        private String[] mPicUrls;

        OnPicClickListener(int currentIndex, String[] picUrls) {
            this.mCurrentIndex = currentIndex;
            this.mPicUrls = picUrls;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), LargePicActivity.class);
            intent.putExtra("cur", mCurrentIndex);
            intent.putExtra("urls", mPicUrls);
            v.getContext().startActivity(intent);
        }
    }
}

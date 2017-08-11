package com.unixtrong.anbo.entity;

import com.unixtrong.anbo.tools.Lg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by danyun on 2017/8/5
 */

public class Feed {
    public static final int TYPE_RETWEET = 1;
    public static final int TYPE_PICTURE = 2;
    public static final int TYPE_MULTI_PICTURE = 3;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
    /**
     * 对应 JSON 中的 user 字段
     * 发送者的用户信息
     */
    private User mUser;
    /**
     * 对应 JSON 中的 id 字段
     * 本条微博的唯一 id
     */
    private long mId;
    /**
     * 对应 JSON 中的 text 字段
     * F微博的文本内容
     */
    private String mContent;
    /**
     * 对应 JSON 中的 created_at 字段
     * 微博的发布时间
     */
    private Date mDate;
    /**
     * 对应 JSON 中的 pic_urls 字段
     * 图片微博才会出现，表示图片地址
     */
    private String[] mPics;
    /**
     * 对应 JSON 中的 retweeted_status 字段
     * 转发时才会出现，表示原微博
     */
    private Feed mRetweet;

    private int mType;

    public static Feed fill(JSONObject jsonObject) {
        Feed feed = new Feed();
        if (!jsonObject.isNull("id")) {
            feed.setId(jsonObject.optInt("id"));
        }
        if (!jsonObject.isNull("created_at")) {
            try {
                String createdAt = jsonObject.optString("created_at");
                // API 返回的时间格式需要解析为 Java 中的日期对象，方便后续时间的逻辑处理和展示
                feed.setDate(DATE_FORMAT.parse(createdAt));
            } catch (ParseException e) {
                Lg.warn(e);
            }
        }
        if (!jsonObject.isNull("text")) {
            feed.setContent(jsonObject.optString("text"));
        }
        if (!jsonObject.isNull("user")) {
            feed.setUser(User.fill(jsonObject.optJSONObject("user")));
        }
        if (!jsonObject.isNull("pic_urls")) {
            JSONArray jsonArray = jsonObject.optJSONArray("pic_urls");
            String[] pics = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject picJson = jsonArray.optJSONObject(i);
                pics[i] = picJson.optString("thumbnail_pic");
            }
            if (pics.length == 1) {
                feed.setPics(pics);
                feed.setType(TYPE_PICTURE);
            } else if (pics.length > 1) {
                feed.setPics(pics);
                feed.setType(TYPE_MULTI_PICTURE);
            }

        }
        if (!jsonObject.isNull("retweeted_status")) {
            feed.setRetweet(Feed.fill(jsonObject.optJSONObject("retweeted_status")));
            feed.setType(TYPE_RETWEET);
        }
        return feed;
    }

    public static List<Feed> fillList(JSONArray jsonArray) {
        List<Feed> feeds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            feeds.add(fill(jsonArray.optJSONObject(i)));
        }
        return feeds;
    }

    public User getUser() {
        return mUser;
    }

    public Feed setUser(User user) {
        mUser = user;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Feed setContent(String content) {
        mContent = content;
        return this;
    }

    public Date getDate() {
        return mDate;
    }

    public Feed setDate(Date date) {
        mDate = date;
        return this;
    }

    public long getId() {
        return mId;
    }

    public Feed setId(long id) {
        mId = id;
        return this;
    }

    public Feed getRetweet() {
        return mRetweet;
    }

    public Feed setRetweet(Feed retweet) {
        mRetweet = retweet;
        return this;
    }

    public String[] getPics() {
        return mPics;
    }

    public Feed setPics(String[] pics) {
        mPics = pics;
        return this;
    }

    public int getType() {
        return mType;
    }

    public Feed setType(int type) {
        mType = type;
        return this;
    }
}

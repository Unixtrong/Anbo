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
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

    private User mUser;
    /**
     * id
     */
    private long mId;
    /**
     * text
     */
    private String mContent;
    /**
     * created_at
     */
    private Date mDate;

    private Retweet mRetweet;

    public static Feed fill(JSONObject jsonObject) {
        Feed feed = new Feed();
        if (jsonObject.has("id")) {
            feed.setId(jsonObject.optInt("id"));
        }
        if (jsonObject.has("created_at")) {
            try {
                String createdAt = jsonObject.optString("created_at");
                feed.setDate(DATE_FORMAT.parse(createdAt));
            } catch (ParseException e) {
                Lg.warn(e);
            }
        }
        if (jsonObject.has("text")) {
            feed.setContent(jsonObject.optString("text"));
        }
        if (jsonObject.has("user")) {
            feed.setUser(User.fill(jsonObject.optJSONObject("user")));
        }
        if (jsonObject.has("retweeted_status")) {
            feed.setRetweet(Retweet.fill(jsonObject.optJSONObject("retweeted_status")));
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

    public Retweet getRetweet() {
        return mRetweet;
    }

    public Feed setRetweet(Retweet retweet) {
        mRetweet = retweet;
        return this;
    }
}

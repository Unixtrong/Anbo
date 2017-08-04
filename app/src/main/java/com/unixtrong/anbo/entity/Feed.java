package com.unixtrong.anbo.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danyun on 2017/8/5
 */

public class Feed {
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
    private String mTime;

    public static Feed fill(JSONObject jsonObject) {
        Feed feed = new Feed();
        if (jsonObject.has("id")) {
            feed.setId(jsonObject.optInt("id"));
        }
        if (jsonObject.has("created_at")) {
            feed.setTime(jsonObject.optString("created_at"));
        }
        if (jsonObject.has("text")) {
            feed.setContent(jsonObject.optString("text"));
        }
        if (jsonObject.has("user")) {
            feed.setUser(User.fill(jsonObject.optJSONObject("user")));
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

    public String getTime() {
        return mTime;
    }

    public Feed setTime(String time) {
        mTime = time;
        return this;
    }

    public long getId() {
        return mId;
    }

    public Feed setId(long id) {
        mId = id;
        return this;
    }
}

package com.unixtrong.anbo.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danyun on 2017/8/6
 */

public class Retweet {
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

    public static Retweet fill(JSONObject jsonObject) {
        Retweet retweet = new Retweet();
        if (jsonObject.has("id")) {
            retweet.setId(jsonObject.optInt("id"));
        }
        if (jsonObject.has("created_at")) {
            retweet.setTime(jsonObject.optString("created_at"));
        }
        if (jsonObject.has("text")) {
            retweet.setContent(jsonObject.optString("text"));
        }
        if (jsonObject.has("user")) {
            retweet.setUser(User.fill(jsonObject.optJSONObject("user")));
        }
        return retweet;
    }

    public static List<Retweet> fillList(JSONArray jsonArray) {
        List<Retweet> feeds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            feeds.add(fill(jsonArray.optJSONObject(i)));
        }
        return feeds;
    }

    public User getUser() {
        return mUser;
    }

    public Retweet setUser(User user) {
        mUser = user;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Retweet setContent(String content) {
        mContent = content;
        return this;
    }

    public String getTime() {
        return mTime;
    }

    public Retweet setTime(String time) {
        mTime = time;
        return this;
    }

    public long getId() {
        return mId;
    }

    public Retweet setId(long id) {
        mId = id;
        return this;
    }

}

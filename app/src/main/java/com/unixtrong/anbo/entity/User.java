package com.unixtrong.anbo.entity;

import org.json.JSONObject;

/**
 * Created by danyun on 2017/8/5
 */

public class User {

    /**
     * id
     */
    private long mId;
    /**
     * name
     */
    private String mName;
    /**
     * profile_image_url
     */
    private String mAvatar;

    public static User fill(JSONObject jsonObject) {
        User user = new User();
        if (jsonObject.has("id")) {
            user.setId(jsonObject.optInt("id"));
        }
        if (jsonObject.has("name")) {
            user.setName(jsonObject.optString("name"));
        }
        if (jsonObject.has("profile_image_url")) {
            user.setAvatar(jsonObject.optString("profile_image_url"));
        }
        return user;
    }

    public String getName() {
        return mName;
    }

    public User setName(String name) {
        mName = name;
        return this;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public User setAvatar(String avatar) {
        mAvatar = avatar;
        return this;
    }

    public long getId() {
        return mId;
    }

    public User setId(long id) {
        mId = id;
        return this;
    }
}

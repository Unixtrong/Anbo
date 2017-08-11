package com.unixtrong.anbo.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author(s): danyun
 * Date: 2017/8/12
 */
public class Group {
    private int mId;
    private String mName;
    private int mCount;

    public static Group fill(JSONObject jsonObject) {
        Group group = new Group();
        if (!jsonObject.isNull("id")) {
            group.setId(jsonObject.optInt("id"));
        }
        if (!jsonObject.isNull("name")) {
            group.setName(jsonObject.optString("name"));
        }
        if (!jsonObject.isNull("count")) {
            group.setCount(jsonObject.optInt("count"));
        }
        return group;
    }

    public static List<Group> fillList(JSONArray jsonArray) {
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            groups.add(fill(jsonArray.optJSONObject(i)));
        }
        return groups;
    }

    public int getId() {
        return mId;
    }

    public Group setId(int id) {
        mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Group setName(String name) {
        mName = name;
        return this;
    }

    public int getCount() {
        return mCount;
    }

    public Group setCount(int count) {
        mCount = count;
        return this;
    }
}

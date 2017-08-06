package com.unixtrong.anbo.handler;

import android.content.Context;

/**
 * Created by danyun on 2017/8/5
 */

public class PreferHelper {
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * 获取当前用户保存的 token
     */
    public static String getAccessToken(Context context) {
        return CommonPrefer.getString(context, ACCESS_TOKEN, "");
    }

    /**
     * 保存当前用户的 token
     */
    public static void setAccessToken(Context context, String token) {
        CommonPrefer.setString(context, ACCESS_TOKEN, token);
    }
}

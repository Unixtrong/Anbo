package com.unixtrong.anbo.handler;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by danyun on 2017/8/5
 */

public class AppInfo {
    /**
     * 判断用户是否已登录
     */
    public static boolean hasSignIn(Context context) {
        String token = PreferHelper.getAccessToken(context.getApplicationContext());
        return !TextUtils.isEmpty(token);
    }

    /**
     * 获取当前用户的微博访问 token
     */
    public static String getAccessToken(Context context) {
        return PreferHelper.getAccessToken(context.getApplicationContext());
    }

    /**
     * 设置并保存当前用户的微博访问 token
     */
    public static void setAccessToken(Context context, String token) {
        PreferHelper.setAccessToken(context, token);
    }
}

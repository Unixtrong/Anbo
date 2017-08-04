package com.unixtrong.anbo.handler;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by danyun on 2017/8/5
 */

public class AppInfo {
    public static boolean hasSignIn(Context context) {
        String token = PreferHelper.getAccessToken(context.getApplicationContext());
        return !TextUtils.isEmpty(token);
    }

    public static String getAccessToken(Context context) {
        return PreferHelper.getAccessToken(context.getApplicationContext());
    }

    public static void setAccessToken(Context context, String token) {
        PreferHelper.setAccessToken(context, token);
    }
}

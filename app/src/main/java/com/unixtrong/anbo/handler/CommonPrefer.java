package com.unixtrong.anbo.handler;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by danyun on 2017/8/5
 */

class CommonPrefer {

    public static String getString(Context context, String key, String defaultValue) {
        return getPreferences(context).getString(key, defaultValue);
    }

    public static void setString(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("common_preference", Context.MODE_PRIVATE);
    }
}

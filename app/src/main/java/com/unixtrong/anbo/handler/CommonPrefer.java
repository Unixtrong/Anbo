package com.unixtrong.anbo.handler;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by danyun on 2017/8/5
 */

class CommonPrefer {

    /**
     * 获取 SP 文件中的文本数据
     *
     * @param key          文本数据保存时的 key
     * @param defaultValue 未获取到时返回的默认数据
     * @return 文本数据的 value
     */
    public static String getString(Context context, String key, String defaultValue) {
        return getPreferences(context).getString(key, defaultValue);
    }

    /**
     * 保存 SP 文件中的文本数据
     *
     * @param key   文本数据保存时的 key
     * @param value 文本数据保存时的 value
     */
    public static void setString(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }

    /**
     * 获取本地 SP 文件对象
     *
     * @return 本地 SP 文件对象
     */
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("common_preference", Context.MODE_PRIVATE);
    }
}

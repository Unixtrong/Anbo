package com.unixtrong.anbo.tools;

import android.util.Log;

import java.util.Locale;

/**
 * Created by danyun on 2017/8/5
 */

public class Lg {

    private static final String TAG = "anbo";
    private static boolean enable = false;

    /**
     * 设置日志开关
     *
     * @param enable 为 true 时，日志开启，否则关闭
     */
    public static void setEnable(boolean enable) {
        Lg.enable = enable;
    }

    public static void debug(String log) {
        if (enable) {
            Log.d(TAG, format(log));
        }
    }

    public static void debug(String log, Object... params) {
        if (enable) {
            Log.d(TAG, format(String.format(Locale.getDefault(), log, params)));
        }
    }

    public static void warn(Throwable t) {
        if (enable) {
            Log.w(TAG, format("\n"), t);
        }
    }

    private static String format(String log) {
        StackTraceElement ste = new Throwable().getStackTrace()[2];
        String[] clzPackage = ste.getClassName().split("\\.");
        String clazz = clzPackage[clzPackage.length - 1];
        String method = ste.getMethodName();
        int lineNum = ste.getLineNumber();
        log = log == null ? "" : log;
        return String.format("%s:%s %s, %s", clazz, lineNum, method, log);
    }
}

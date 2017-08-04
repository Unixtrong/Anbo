package com.unixtrong.anbo.tools;

import android.util.Log;

/**
 * Created by danyun on 2017/8/5
 */

public class Lg {

    private static final String TAG = "anbo";
    private static boolean enable = false;

    public static void setEnable(boolean enable) {
        Lg.enable = enable;
    }

    public static void debug(String log) {
        if (enable) {
            Log.d(TAG, format(log));
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
        return String.format("%s %s(%s): %s", clazz, method, lineNum, log);
    }
}

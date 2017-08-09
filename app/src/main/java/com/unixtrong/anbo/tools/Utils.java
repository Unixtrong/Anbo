package com.unixtrong.anbo.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * Created by danyun on 2017/8/8
 */

public class Utils {

    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (int) (1024L * 1024L * memoryClass / 7);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    public static String md5(String text) {
        byte[] buffBytes = md5(text.getBytes());
        if (buffBytes == null) return "";
        StringBuilder result = new StringBuilder();
        for (byte buffByte : buffBytes) {
            if ((buffByte & 0xff) < 0x10) {
                result.append("0");
            }
            result.append(Long.toString(buffByte & 0xff, 16));
        }
        return result.toString();
    }

    public static byte[] md5(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            Lg.warn(e);
        }
        return null;
    }

    public static DisplayMetrics getScreenSize(Context context) {
        WindowManager wm = Utils.getService(context, Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }
}

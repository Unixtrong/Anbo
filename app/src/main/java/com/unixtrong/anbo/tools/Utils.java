package com.unixtrong.anbo.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

    public static int dip(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    /**
     * 按文件路径解析 Bitmap，解析时宽高不能超出指定参数。防止解析过大的图片，造成内存溢出，导致崩溃。
     *
     * @param path      文件路径
     * @param reqWidth  图宽不得超过该长度
     * @param reqHeight 图高不得超过该长度
     * @return 返回压缩后的 Bitmap
     */
    public static Bitmap decodeToBitmap(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options = calculateInSampleSize(options, reqWidth, reqHeight);
        return BitmapFactory.decodeFile(path, options);
    }

    private static BitmapFactory.Options calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最大的比率作为 inSampleSize 的值
            inSampleSize = Math.max(heightRatio, widthRatio);
        }
        Lg.debug("h: " + height + ", w: " + width + ", s: " + inSampleSize);
        // 设置压缩比例
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return options;
    }
}

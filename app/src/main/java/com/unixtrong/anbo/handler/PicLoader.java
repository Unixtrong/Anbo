package com.unixtrong.anbo.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.unixtrong.anbo.tools.Downloader;
import com.unixtrong.anbo.tools.Utils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by danyun on 2017/8/8
 */

public class PicLoader {
    private static final int HANDLER_ON_DISK = 1;
    private static final int HANDLER_ON_REMOTE = 2;

    private static volatile PicLoader sSingleton = null;
    private final Context mContext;
    private LruCache<String, Bitmap> mLruCache;
    private ExecutorService mExecutor;
    private Handler mHandler;

    private PicLoader(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        mContext = context.getApplicationContext();
        mLruCache = new LruCache<>(Utils.calculateMemoryCacheSize(mContext));
        mExecutor = Executors.newCachedThreadPool();
        mHandler = new ActionHandler(Looper.getMainLooper());
    }

    public static PicLoader with(Context context) {
        if (sSingleton == null) {
            synchronized (PicLoader.class) {
                if (sSingleton == null) {
                    sSingleton = new PicLoader(context);
                }
            }
        }
        return sSingleton;
    }

    public PicLoader bind(String url, ImageView target, int defaultRes) {
        if (TextUtils.isEmpty(url)) {
            target.setImageResource(defaultRes);
        } else {
            final String picKey = Utils.md5(url);
            Bitmap bitmap = mLruCache.get(picKey);
            if (bitmap != null) {
                target.setImageBitmap(bitmap);
            } else {
                target.setImageResource(defaultRes);
                mExecutor.execute(new LoadRunnable(new Pic(picKey, url, target)));
            }
        }
        return this;
    }

    private File cacheFile(String name) {
        return new File(mContext.getExternalCacheDir(), name);
    }

    private class Pic {
        String mKey;
        String mUrl;
        ImageView mTarget;

        Pic(String key, String url, ImageView target) {
            mKey = key;
            mUrl = url;
            mTarget = target;
        }
    }

    private class LoadRunnable implements Runnable {
        private Pic mPic;

        private LoadRunnable(Pic pic) {
            mPic = pic;
        }

        @Override
        public void run() {
            File file = cacheFile(mPic.mKey);
            Bitmap fileBitmap;
            if (file.exists() && (fileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath())) != null) {
                mLruCache.put(mPic.mKey, fileBitmap);
                mHandler.obtainMessage(HANDLER_ON_DISK, mPic).sendToTarget();
            } else {
                file = Downloader.start(mPic.mUrl, file.getAbsolutePath());
                Bitmap downloadBitmap;
                if (file != null && file.exists() && (downloadBitmap = BitmapFactory.decodeFile(file.getAbsolutePath())) != null) {
                    mLruCache.put(mPic.mKey, downloadBitmap);
                    mHandler.obtainMessage(HANDLER_ON_REMOTE, mPic).sendToTarget();
                }
            }
        }

    }

    private class ActionHandler extends Handler {

        ActionHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_ON_DISK:
                case HANDLER_ON_REMOTE:
                    Pic pic = (Pic) msg.obj;
                    pic.mTarget.setImageBitmap(mLruCache.get(pic.mKey));
                    break;
            }
        }
    }
}